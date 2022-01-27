package mb.ccbench

import mb.ccbench.utils.runParse
import mb.ccbench.utils.sample
import mb.ccbench.utils.withExtension
import mb.ccbench.utils.withName
import mb.common.region.Region
import mb.common.result.Result
import mb.common.util.ListView
import mb.constraint.pie.ConstraintAnalyzeTaskDef
import mb.jsglr.pie.JsglrParseTaskDef
import mb.nabl2.terms.stratego.TermOrigin
import mb.pie.api.*
import mb.resource.hierarchical.ResourcePath
import mb.resource.text.TextResourceRegistry
import mb.spree.green.TreePath
import mb.stratego.common.StrategoRuntime
import me.tongfei.progressbar.ProgressBar
import mu.KotlinLogging
import org.spoofax.interpreter.terms.*
import org.spoofax.terms.io.SimpleTextTermWriter
import org.spoofax.terms.util.TermUtils
import java.io.Serializable
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.inject.Provider

/**
 * Builds a single benchmark.
 */
abstract class BuildBenchmarkTask(
    private val parseTaskDef: JsglrParseTaskDef,
    private val strategoRuntimeProvider: Provider<StrategoRuntime>,
    private val getSourceFilesTaskDef: TaskDef<ResourcePath, ListView<ResourcePath>>,
    private val analyzeTaskDef: ConstraintAnalyzeTaskDef,
//    private val analyzeTaskDef: ConstraintAnalyzeMultiTaskDef,

    private val preAnalyzeStrategyName: String,
    private val postAnalyzeStrategyName: String,
    private val upgradePlaceholdersStrategyName: String,
    private val downgradePlaceholdersStrategyName: String,
    private val isInjStrategyName: String,
    private val ppPartialStrategyName: String,

    private val textResourceRegistry: TextResourceRegistry,
    private val termWriter: SimpleTextTermWriter,
) : TaskDef<BuildBenchmarkTask.Input, ListView<TestCase>> {

    /**
     * The input arguments for the task.
     *
     * @property projectDir the project directory
     * @property inputFile the input language file, relative to the project directory
     * @property testCaseDir the test case directory
     */
    data class Input(
        val projectDir: Path,
        val inputFile: Path,
        val testCaseDir: Path,
        val sample: Int?,
        val rnd: Random,
    ): Serializable

    private val log = KotlinLogging.logger {}

    /**
     * Runs this task.
     *
     * @param inputFile the task input file
     * @return the benchmark
     */
    fun run(pie: Pie, projectDir: Path, inputFile: Path, testCaseDir: Path, sample: Int?, rnd: Random): List<TestCase> {
        pie.newSession().use { session ->
            val topDownSession = session.updateAffectedBy(emptySet())
            return topDownSession.require(this.createTask(Input(projectDir, inputFile, testCaseDir, sample, rnd))).toList()
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//                emptyList()
//            }
        }
    }

    override fun getId(): String = BuildBenchmarkTask::class.java.name

    override fun exec(ctx: ExecContext, input: Input): ListView<TestCase> {
        val prettyPrintMode = PrettyPrintingMode.OnlyPlaceholder
        val runtime = strategoRuntimeProvider.get()
//        val runtime = ctx.require<None, OutTransient<Provider<StrategoRuntime>>>(getStrategoRuntimeProviderTaskDef, None.instance).getValue().get()

        val originalName = input.inputFile.withExtension("").toString()
        val resInputFile = input.projectDir.resolve(input.inputFile)
        val projectDirResource = ctx.require(input.projectDir)
        val inputResource = ctx.require(resInputFile)

        val ast: IStrategoTerm
        try {
            // Parse the input
            ast = parseTaskDef.runParse(ctx, inputResource.key, inputResource.key)
        } catch (ex: Exception) {
            log.warn { "Failed to parse $originalName" }
            ex.printStackTrace()
            return ListView.of()
        }

        try {
            // Analyze the file
            val result = ctx.require(analyzeTaskDef, ConstraintAnalyzeTaskDef.Input(
                inputResource.key
            ) { Result.ofOk<IStrategoTerm, Exception>(ast) }).unwrap()
            if (result.result.messages.containsError()) {
                log.warn { "Failed to analyze $originalName: " + result.result.messages.filter { it.isErrorOrHigher }.joinToString()}
                return ListView.of()
            }
//            // Analyze the project
//            val analyzeInput = ConstraintAnalyzeMultiTaskDef.Input(
//                projectDirResource.key,
//                parseTask.createRecoverableMultiAstSupplierFunction(getSourceFilesTask.createFunction())
//            )
//            val result = ctx.require(analyzeTask, analyzeInput).unwrap()
//            if (result.result.messages.containsError()) {
//                log.warn { "Failed to analyze $originalName: " + result.result.messages.messagesWithoutKey.filter { it.isErrorOrHigher }.joinToString()}
//                return ListView.of()
//            }
        } catch (ex: Exception) {
            log.warn(ex) { "Failed to analyze $originalName" }
            ex.printStackTrace()
            return ListView.of()
        }

        val ppAst = if (prettyPrintMode == PrettyPrintingMode.All) {
            // We pretty-print and parse the input resource again here, such that are sure
            // that the offset of the term is the same in the incomplete pretty-printed AST
            val ppStr = prettyPrint(runtime, ast)
            val ppResource = textResourceRegistry.createResource(ppStr)
            parseTaskDef.runParse(ctx, ppResource.key, inputResource.key)
        } else {
            ast
        }
        val explicatedAst = explicate(runtime, ppAst)

        // Get all possible incomplete ASTs
        val incompleteAsts = buildIncompleteAsts(explicatedAst, runtime.termFactory)

        // Downgrade the placeholders in the incomplete ASTs, and pretty-print them
        val prettyPrintedAsts = incompleteAsts.mapNotNull { it.map { term ->
            val downgradedAst = downgrade(runtime, term)
            if (prettyPrintMode == PrettyPrintingMode.All) {
                prettyPrint(runtime, implicate(runtime, downgradedAst))
            } else {
                // Get the downgraded placeholder term (so we know the Sort)
                val placeholderTerm = downgradedAst.resolve(it.placeholderPath)
                val placeholderRepr = if (prettyPrintMode == PrettyPrintingMode.OnlyPlaceholder) {
                    // pretty-print it (so we know its representation)
                    prettyPrint(runtime, placeholderTerm)
                } else {
                    // think of a substitute representation
                    val sort = getSortFromPlaceholderTerm(placeholderTerm)
                    "[[$sort]]"
                }
                // and replace it in the original text
                val inputString = inputResource.readString()
                inputString.substring(0, it.startOffset) + placeholderRepr + inputString.substring(it.endOffset)
            }
        } }

        // Construct test cases and write the files to the test cases directory
        val testCases = mutableListOf<TestCase>()
        Files.createDirectories(input.testCaseDir.resolve(input.inputFile).parent)
        val indexedAsts = prettyPrintedAsts.withIndex()
        val sampledAsts = input.sample?.let { indexedAsts.sample(it, input.rnd) } ?: indexedAsts
        for((i, case) in ProgressBar.wrap(sampledAsts, "Input files")) {
            val name = input.inputFile.withName { "$it-$i" }.withExtension("").toString()

            log.trace { "Writing $name..." }
            // Write the pretty-printed AST to file
            val outputFile = input.testCaseDir.resolve(input.inputFile.withName { "$it-$i" })
            ctx.provide(outputFile)
            Files.writeString(outputFile, case.value)

            // Write the expected AST to file
            val expectedFile = outputFile.withName { "$it-expected" }.withExtension { "$it.aterm" }
            ctx.provide(expectedFile)
            termWriter.writeToPath(case.expectedAst, expectedFile)
//            Files.writeString(expectedFile, TermToString.toString(case.expectedAst))

            val placeholderRegion = getPlaceholderRegion(case.value)
            if (placeholderRegion == null) {
                log.warn { "Placeholder not found. Skipped $name." }
                continue
            }

            // Add the test case
            testCases.add(
                TestCase(
                    name,
                    input.inputFile,
                    input.testCaseDir.relativize(outputFile),
                    placeholderRegion.startOffset,
                    input.testCaseDir.relativize(expectedFile),
                    case.expectsLiteral,
                )
            )
            log.debug { "Wrote $name." }
        }

        return ListView.of(testCases)
    }

    /**
     * Takes a term and produces all possible combinations of this term with a placeholder.
     *
     * @param term the term
     * @return a sequence of all possible variants of this term with a placeholder
     */
    private fun buildIncompleteAsts(term: IStrategoTerm, factory: ITermFactory): List<TestCaseInfo<IStrategoTerm>> {
        // Replace the term with a placeholder
        val placeholderTerm = makePlaceholder("x", factory)
        val thisTestCase = TestCaseInfo(
            placeholderTerm,
            placeholderTerm,
            TreePath.empty(),
            getStartOffsetRecursive(term),
            getEndOffsetRecursive(term),
            term,
            expectsLiteral = (term.type == TermType.STRING)
        )
        // or replace a subterm with all possible sub-asts with a placeholder
        return listOf(thisTestCase) + term.subterms.flatMapIndexed { i, subTerm ->
            buildIncompleteAsts(subTerm, factory).map { (newSubTerm, placeholderTerm, placeholderPath, startOffset, endOffset, expectedAst, expectsLiteral) ->
                TestCaseInfo(term.withSubterm(i, newSubTerm, factory), placeholderTerm, placeholderPath.prepend(i), startOffset, endOffset, expectedAst, expectsLiteral)
            }
        }
    }

    /**
     * Replaces the subterm with the specified index in the term.
     *
     * @param index the zero-based index of the subterm to replace
     * @return the new term, with its subterm replaces
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T: IStrategoTerm> T.withSubterm(index: Int, term: IStrategoTerm, factory: ITermFactory): T {
        require(index in 0..this.subtermCount)
        val newSubterms = this.subterms.toTypedArray()
        newSubterms[index] = term
        return when (this) {
            is IStrategoAppl -> factory.makeAppl(this.constructor, newSubterms, this.annotations) as T
            is IStrategoList -> factory.makeList(newSubterms, this.annotations) as T
            is IStrategoTuple -> factory.makeTuple(newSubterms, this.annotations) as T
            else -> throw IllegalStateException("This should not happen.")
        }
    }

    // TODO: Don't use a heuristic
    private val placeholderRegex = Regex("\\[\\[\\w+\\]\\]")

    /**
     * Determines whether the given string contains a placeholder.
     */
    private fun hasPlaceholder(text: String): Boolean {
        // TODO: Don't use a heuristic
        return text.contains(placeholderRegex)
    }

    /**
     * Makes a placeholder.
     *
     * @param name the name of the placeholder, it can be anything
     * @return the created placeholder
     */
    private fun makePlaceholder(name: String, factory: ITermFactory): IStrategoPlaceholder {
        return factory.makePlaceholder(factory.makeString(name))
    }

    /**
     * Determines the region of a placeholder.
     *
     * @return the region of the placeholder; or `null` if not found
     */
    private fun getPlaceholderRegion(text: String): Region? {
        // TODO: Don't use a heuristic
        val range = placeholderRegex.find(text)?.range ?: return null
        return Region.fromOffsets(range.first, range.last + 1)
    }

    /**
     * Explicates the term.
     *
     * @param runtime the Stratego runtime
     * @param term the term
     * @return the downgraded term
     */
    private fun explicate(runtime: StrategoRuntime, term: IStrategoTerm): IStrategoTerm {
        return runtime.invoke(preAnalyzeStrategyName, term)
    }

    /**
     * Implicates the term.
     *
     * @param runtime the Stratego runtime
     * @param term the term
     * @return the downgraded term
     */
    private fun implicate(runtime: StrategoRuntime, term: IStrategoTerm): IStrategoTerm {
        return runtime.invoke(postAnalyzeStrategyName, term)
    }

    /**
     * Upgrades the placeholders in the term.
     *
     * @param runtime the Stratego runtime
     * @param term the term
     * @return the downgraded term
     */
    private fun upgrade(runtime: StrategoRuntime, term: IStrategoTerm): IStrategoTerm {
        return runtime.invoke(upgradePlaceholdersStrategyName, term)
    }

    /**
     * Downgrades the placeholders in the term.
     *
     * @param runtime the Stratego runtime
     * @param term the term
     * @return the downgraded term
     */
    private fun downgrade(runtime: StrategoRuntime, term: IStrategoTerm): IStrategoTerm {
        return runtime.invoke(downgradePlaceholdersStrategyName, term)
    }

    /**
     * Pretty-prints the term.
     *
     * @param runtime the Stratego runtime
     * @param term the term
     * @return the pretty-printed term
     */
    private fun prettyPrint(runtime: StrategoRuntime, term: IStrategoTerm): String {
        return TermUtils.toJavaString(runtime.invoke(ppPartialStrategyName, term))
    }

    /**
     * Gets the start offset the specified term.
     *
     * @param term the term
     * @return the term's start offset
     */
    private fun getStartOffset(term: IStrategoTerm): Int = tryGetStartOffset(term) ?: throw NullPointerException("Could not determine start offset of term: $term")

    /**
     * Attempts to get the start offset the specified term.
     *
     * @param term the term
     * @return the term's start offset; or `null` when it could not be determined
     */
    private fun tryGetStartOffset(term: IStrategoTerm): Int? {
        val origin = TermOrigin.get(term).orElse(null) ?: return null
        val imploderAttachment = origin.imploderAttachment
        // We get the zero-based offset of the first character in the token
        return imploderAttachment.leftToken.startOffset
    }

    /**
     * Gets the end offset the specified term.
     *
     * @param term the term
     * @return the term's end offset
     */
    private fun getEndOffset(term: IStrategoTerm): Int = tryGetEndOffset(term) ?: throw NullPointerException("Could not determine end offset of term: $term")

    /**
     * Attempts to get the end offset the specified term.
     *
     * @param term the term
     * @return the term's end offset; or `null` when it could not be determined
     */
    private fun tryGetEndOffset(term: IStrategoTerm): Int? {
        val origin = TermOrigin.get(term).orElse(null) ?: return null
        val imploderAttachment = origin.imploderAttachment
        // We get the zero-based offset just after the last character in the token
        return imploderAttachment.rightToken.endOffset + 1
    }

    /**
     * Gets the start offset the specified term or a descendant left-most term.
     *
     * @param term the term
     * @return the term's start offset
     */
    private fun getStartOffsetRecursive(term: IStrategoTerm): Int = tryGetStartOffsetRecursive(term) ?: throw NullPointerException("Could not determine start offset of term: $term")

    /**
     * Attempts to get the start offset the specified term or a descendant left-most term.
     *
     * @param term the term
     * @return the term's start offset; or `null` when it could not be determined
     */
    private fun tryGetStartOffsetRecursive(term: IStrategoTerm): Int? {
        var currentTerm: IStrategoTerm = term
        while (true) {
            val startOffset = tryGetStartOffset(currentTerm)
            if (startOffset != null) return startOffset
            if (currentTerm.subtermCount == 0) break
            currentTerm = currentTerm.getSubterm(0) // left-most term
        }
        return null
    }

    private fun getEndOffsetRecursive(term: IStrategoTerm): Int = tryGetEndOffsetRecursive(term) ?: throw NullPointerException("Could not determine end offset of term: $term")

    /**
     * Attempts to get the end offset the specified term or a descendant right-most term.
     *
     * @param term the term
     * @return the term's end offset; or `null` when it could not be determined
     */
    private fun tryGetEndOffsetRecursive(term: IStrategoTerm): Int? {
        var currentTerm: IStrategoTerm = term
        while (true) {
            val endOffset = tryGetEndOffset(currentTerm)
            if (endOffset != null) return endOffset
            if (currentTerm.subtermCount == 0) break
            currentTerm = currentTerm.getSubterm(currentTerm.subtermCount - 1) // right-most term
        }
        return null
    }

    /**
     * Gets the sort of a placeholder term.
     */
    private fun getSortFromPlaceholderTerm(term: IStrategoTerm): String {
        if (term !is IStrategoAppl) throw IllegalStateException("Expected placeholder term, got: $term")
        if (!term.name.endsWith("-Plhdr")) throw IllegalStateException("Expected placeholder term, got: $term")
        return term.name.substring(0, term.name.length - "-Plhdr".length)
    }

    /**
     * A value and a placeholder offset.
     *
     * @property value the value
     * @property placeholderTerm the placeholder term
     * @property placeholderPath the path to the placeholder
     * @property startOffset the start offset of the placeholder
     * @property endOffset the end offset of the placeholder
     * @property expectedAst the expected AST
     * @property expectsLiteral whether to expect a literal at the placeholder
     */
    data class TestCaseInfo<out T>(
        val value: T,
        val placeholderTerm: IStrategoTerm,
        val placeholderPath: TreePath<IStrategoTerm>,
        val startOffset: Int,
        val endOffset: Int,
        val expectedAst: IStrategoTerm,
        val expectsLiteral: Boolean,
    ) {
        fun <R> map(f: (T) -> R?): TestCaseInfo<R>? {
            val newValue = f(value) ?: return null
            return TestCaseInfo(newValue, placeholderTerm, placeholderPath, startOffset, endOffset, expectedAst, expectsLiteral)
        }
    }

    enum class PrettyPrintingMode {
        /** Perform no pretty-printing. */
        None,
        /** Pretty-print the placeholder only. */
        OnlyPlaceholder,
        /** Pretty-print everything. */
        All,
    }

    /**
     * Resolves a path starting from this term.
     *
     * @param path the path to resolve
     * @return the resulting term
     * @throws IndexOutOfBoundsException the path is invalid
     */
    private fun IStrategoTerm.resolve(path: TreePath<IStrategoTerm>): IStrategoTerm {
        val tail = path.tail ?: return this
        val subterm = this.subterms[path.index]
        return subterm.resolve(tail)
    }
}
