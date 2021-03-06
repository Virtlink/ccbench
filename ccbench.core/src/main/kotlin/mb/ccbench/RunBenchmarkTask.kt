package mb.ccbench

import mb.ccbench.results.BenchResult
import mb.ccbench.results.BenchResultKind
import mb.ccbench.results.Timings
import mb.ccbench.utils.runParse
import mb.common.region.Region
import mb.jsglr.pie.JsglrParseTaskDef
import mb.nabl2.terms.IStringTerm
import mb.nabl2.terms.ITerm
import mb.nabl2.terms.ITermVar
import mb.nabl2.terms.matching.TermPattern
import mb.nabl2.terms.substitution.ISubstitution
import mb.pie.api.ExecContext
import mb.pie.api.Pie
import mb.pie.api.TaskDef
import mb.resource.ResourceKey
import mb.statix.codecompletion.TermCodeCompletionItem
import mb.statix.codecompletion.TermCodeCompletionResult
import mb.statix.codecompletion.pie.CodeCompletionTaskDef
import mb.statix.codecompletion.pie.MeasuringCodeCompletionEventHandler
import mu.KotlinLogging
import org.spoofax.interpreter.terms.IStrategoAppl
import org.spoofax.interpreter.terms.IStrategoList
import org.spoofax.interpreter.terms.IStrategoTerm
import org.spoofax.interpreter.terms.IStrategoTuple
import org.spoofax.jsglr.client.imploder.ImploderAttachment
import java.io.Serializable
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.fileSize

/**
 * Runs a single benchmark.
 */
abstract class RunBenchmarkTask(
    private val parseTask: JsglrParseTaskDef,
    private val codeCompletionTask: CodeCompletionTaskDef,
) : TaskDef<RunBenchmarkTask.Input, BenchResult> {

    data class Input(
        val benchmark: Benchmark,
        val testCaseDir: Path,
        val sourceProjectDir: Path,
        val targetProjectDir: Path,
        val testCase: TestCase,
        val expectedTerm: ITerm,
        val completeDeterministic: Boolean,
    ): Serializable

    /**
     * Runs this task.
     *
     * @param benchmarkCase the benchmark to run
     * @return the benchmark result
     */
    fun run(
        pie: Pie,
        benchmark: Benchmark,
        testCaseDir: Path,
        sourceProjectDir: Path,
        targetProjectDir: Path,
        testCase: TestCase,
        expectedTerm: ITerm,
        dstInputResourceKey: ResourceKey,
        completeDeterministic: Boolean,
    ): BenchResult {
        pie.newSession().use { session ->
            val topDownSession = session.updateAffectedBy(setOf(
                dstInputResourceKey
            ))
            val result = topDownSession.requireWithoutObserving(this.createTask(Input(
                benchmark,
                testCaseDir,
                sourceProjectDir,
                targetProjectDir,
                testCase,
                expectedTerm,
                completeDeterministic
            )))
            session.dropCallbacks()
            session.dropStore()
            return result
        }
    }

    companion object {
        /** Number of ns per ms. */
        private val NS_PER_MS: Double = 1000_000.0
    }

    private val log = KotlinLogging.logger {}

    override fun getId(): String = RunBenchmarkTask::class.java.name

    override fun exec(ctx: ExecContext, input: Input): BenchResult {

        var kind: BenchResultKind
        var charSize: Long = -1
        var tokenSize: Long = -1
        var lineSize: Long = -1
        var astSize: Long = -1
        var results: TermCodeCompletionResult? = null
        val eventHandler = MeasuringCodeCompletionEventHandler()
        try {
            log.debug { "Running test ${input.testCase.inputFile}..."}
            val dstInputFile = input.targetProjectDir.resolve(input.testCase.file)

            // We parse the input resource here, such that we don't measure the overhead of parsing the input resource again
            val dstInputResource = ctx.require(dstInputFile)
            charSize = dstInputFile.fileSize()  // Assumes UTF-8
            lineSize = dstInputFile.bufferedReader().lineSequence().count().toLong()
            val ast = parseTask.runParse(ctx, dstInputResource.key, dstInputResource.key)
            tokenSize = ImploderAttachment.getTokenizer(ast).tokenCount.toLong()
            astSize = computeTermSize(ast)

            // Execute code completion
            codeCompletionTask.withEventHandlerProvider { eventHandler }
            results = ctx.require(
                codeCompletionTask, CodeCompletionTaskDef.Input(
                    Region.atOffset(input.testCase.placeholderOffset),
                    dstInputResource.key,
                    ctx.require(input.targetProjectDir).path,
                    input.completeDeterministic,
                )
            ).unwrap() as TermCodeCompletionResult

            kind = if (!results.proposals.isEmpty) {
                val extProposals = results.proposals.filterIsInstance<TermCodeCompletionItem>()
                val success =
                    extProposals.any { tryMatchExpectation(results.placeholder, input.expectedTerm, it.term) }
                if (success) {
                    log.info { "Success. Expected: ${input.expectedTerm}, got: ${extProposals.joinToString { "${it.label} (${it.term})" }}" }
                    BenchResultKind.Success
                } else if (input.expectedTerm is IStringTerm) {
                    log.info { "Success. Expected literal: ${input.expectedTerm}, but got: ${extProposals.joinToString { "${it.label} (${it.term})" }}" }
                    BenchResultKind.Literal
                } else {
                    log.warn { "Fail. Expected: ${input.expectedTerm}, but got: ${extProposals.joinToString { "${it.label} (${it.term})" }}" }
                    BenchResultKind.Failed
                }
            } else if (input.expectedTerm is IStringTerm) {
                log.info { "Success. Expected literal: ${input.expectedTerm}, but got no proposals." }
                BenchResultKind.Literal
            } else {
                log.warn { "Fail. Expected: ${input.expectedTerm}, but got no proposals." }
                BenchResultKind.NoResults
            }
        } catch (ex: IllegalStateException) {
            kind = if (ex.message?.contains("input program validation failed") == true) {
                log.warn { "Fail. Analysis failed: ${ex.message}" }
                BenchResultKind.AnalysisFailed
            } else {
                log.warn(ex) { "Fail. Error running test." }
                BenchResultKind.Error
            }
        }

        val parseTime = (eventHandler.parseTime ?: -1).toDouble() / NS_PER_MS
        val preparationTime = (eventHandler.preparationTime ?: -1).toDouble() / NS_PER_MS
        val analyzeTime = (eventHandler.analysisTime ?: -1).toDouble() / NS_PER_MS
        val codeCompletionTime = (eventHandler.codeCompletionTime ?: -1).toDouble() / NS_PER_MS
        val finishingTime = (eventHandler.finishingTime ?: -1).toDouble() / NS_PER_MS
        val totalTime = (eventHandler.totalTime ?: -1).toDouble() / NS_PER_MS
        return BenchResult(
            input.testCase.name,
            input.testCase.file,
            kind,
            charSize,
            lineSize,
            tokenSize,
            astSize,
            results?.proposals?.size() ?: 0,
            Timings(
                parseTime,
                preparationTime,
                analyzeTime,
                codeCompletionTime,
                finishingTime,
                totalTime,

                parseTime + preparationTime + analyzeTime + finishingTime,

                (0).toDouble() / NS_PER_MS, // TODO
                (0).toDouble() / NS_PER_MS, // TODO
                (0).toDouble() / NS_PER_MS, // TODO
                (0).toDouble() / NS_PER_MS, // TODO
            )
        )
    }

    private fun tryMatchExpectation(
        placeholder: ITermVar,
        expectedTerm: ITerm,
        actualTerm: ITerm,
    ): Boolean {
        // If trying to replace by the same variable indicates that the proposal
        // did not replace the variable by a term.
        if (placeholder == actualTerm) return false
        // If the variable can never be replaced by the actual term, we reject this proposal.
        return trySubstitute(expectedTerm, actualTerm) != null
    }

    private fun trySubstitute(expectedTerm: ITerm, actualTerm: ITerm): ISubstitution.Immutable? {
        // Does the term we got, including variables, match the expected term?
        return TermPattern.P().fromTerm(actualTerm).match(expectedTerm).orElse(null)
    }

    /**
     * Computes the term size, excluding annotations.
     */
    private fun computeTermSize(term: IStrategoTerm): Long {
        return when (term) {
            is IStrategoAppl -> 1 + term.subterms.sumOf { computeTermSize(it) }
            is IStrategoList -> 1 + term.subterms.sumOf { computeTermSize(it) }
            is IStrategoTuple -> 1 + term.subterms.sumOf { computeTermSize(it) }
            else -> 1
        }
    }
}
