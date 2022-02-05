package mb.ccbench

import com.github.ajalt.mordant.animation.progressAnimation
import com.github.ajalt.mordant.rendering.BorderStyle.Companion.SQUARE_DOUBLE_SECTION_SEPARATOR
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.terminal.Terminal
import mb.ccbench.results.BenchResult
import mb.ccbench.results.BenchResultSet
import mb.ccbench.results.BenchmarkSummary
import mb.ccbench.utils.sample
import mb.nabl2.terms.stratego.StrategoTerms
import mb.pie.api.Pie
import mb.resource.fs.FSResource
import mb.stratego.common.StrategoRuntime
import mb.tego.strategies.runtime.MeasuringTegoRuntime
import mb.tego.strategies.runtime.TegoRuntime
import me.tongfei.progressbar.ProgressBar
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.spoofax.interpreter.terms.ITermFactory
import org.spoofax.terms.io.TAFTermReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Provider
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.rendering.TextStyles.*
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.SectionBuilder
import com.github.ajalt.mordant.table.table
import mb.ccbench.results.Timings

/**
 * Runs a benchmark.
 */
abstract class BenchmarkRunner(
    private val pie: Pie,
    private val runBenchmarkTask: RunBenchmarkTask,
    private val strategoRuntimeProvider: Provider<StrategoRuntime>,
    private val tegoRuntime: TegoRuntime,
    private val summarizer: BenchmarkSummarizer,
) {
    private val log = KotlinLogging.logger {}

    /**
     * Runs the benchmark tests.
     *
     * @param name the name of the benchmark (used as the name of the output CSV and YML files)
     * @param benchmark the benchmark, read from the input file
     * @param benchmarkFile the file from which the benchmark was read
     * @param projectDir the directory with the other project files
     * @param tmpProjectDir the directory where the project files and the benchmark test case are copied to run a single benchmark case
     * @param outputDir the directory where the output CSV and YML files are written
     * @param fileNames the names of files whose tests to include in the candidates (or an empty list to include all)
     * @param testNames the names of tests to include in the candidates (or an empty list to include all tests)
     * @param samples the number of tests to sample from the candidates
     * @param warmups the number of warmup tests to run (sampled from the candidates) before running the actual tests
     * @param seed the seed to use for the random number generator for the sampling
     * @param completeDeterministic whether to perform deterministic completion
     */
    @OptIn(ExperimentalStdlibApi::class)
    fun run(
        name: String,
        benchmark: Benchmark,
        benchmarkFile: Path,
        projectDir: Path,
        tmpProjectDir: Path,
        outputDir: Path,
        fileNames: List<String>,
        testNames: List<String>,
        samples: Int?,
        warmups: Int?,
        seed: Long?,
        completeDeterministic: Boolean,
    ): BenchResultSet {
        val factory = strategoRuntimeProvider.get().termFactory
        val testCaseDir = benchmarkFile.parent.resolve(benchmark.testCaseDirectory)

        // Ensure the tmp project directory is empty, and copy the project
        FileUtils.deleteDirectory(tmpProjectDir.toFile())
        Files.createDirectories(tmpProjectDir.parent)
        Files.copy(projectDir, tmpProjectDir)

        if (tegoRuntime is MeasuringTegoRuntime) {
            log.warn { "Measuring Tego runtime." }
        }

        val actualSeed = seed ?: System.nanoTime()

        // Warmup run
        if (warmups != null && warmups > 0) {
            log.info { "Warming up using $warmups runs..." }
            sampleTests(
                name,
                benchmark,
                testCaseDir,
                projectDir,
                tmpProjectDir,
                fileNames,
                testNames,
                warmups,
                // We derive the warmup from the seed,
                // but don't use the same seed (to avoid running some of the same tests already in the warmup)
                actualSeed.rotateLeft(16),
                completeDeterministic,
                factory,
                quiet = true,
            )
            log.debug { "Warmed up using $warmups runs." }
        }

        // Sample and run the tests
        log.info { "Running tests..." }
        val resultSet = sampleTests(
            name,
            benchmark,
            testCaseDir,
            projectDir,
            tmpProjectDir,
            fileNames,
            testNames,
            samples,
            actualSeed,
            completeDeterministic,
            factory
        )
        log.debug { "Ran tests." }

        val dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))

        log.debug { "Writing benchmark results..." }
        val resultsFile = outputDir.resolve("$dateString-$name.csv")
        Files.createDirectories(resultsFile.parent)
        BenchResultSet.writeToCsv(resultSet, resultsFile)
        log.info { "Wrote benchmark results to $resultsFile" }

        // Summarize the results both on the terminal and in the YML file
        summarizer.summarize(
            resultSet,
            outputDir.resolve("$dateString-$name.yml"),
            (tegoRuntime as? MeasuringTegoRuntime)?.measurements,
            actualSeed,
            completeDeterministic
        )
        return resultSet
    }

    /**
     * Samples and runs a number of tests.
     *
     * @param benchmark the benchmark being run
     * @param testCaseDir the directory with the benchmark test cases
     * @param srcProjectDir the directory with the project
     * @param dstProjectDir the directory with a copy of the project, which is modified for each test
     * @param fileNames the names of files whose tests to include in the candidates (or an empty list to include all)
     * @param testNames the names of tests to include in the candidates (or an empty list to include all tests)
     * @param samples the number of tests to run
     * @param seed the seed for the sampler
     * @param completeDeterministic whether to do deterministic completion
     * @param factory the term factory
     * @param quiet whether to perform a quiet run
     * @return the benchmark results
     */
    private fun sampleTests(
        name: String,
        benchmark: Benchmark,
        testCaseDir: Path,
        srcProjectDir: Path,
        dstProjectDir: Path,
        fileNames: List<String>,
        testNames: List<String>,
        samples: Int?,
        seed: Long,
        completeDeterministic: Boolean,
        factory: ITermFactory,
        quiet: Boolean = false,
    ): BenchResultSet {
        val rnd = Random(seed)

        // Run the tests
        val results = mutableListOf<BenchResult>()

        // Pick a random sample of test cases, or randomize the order
        val candidates = if (fileNames.isEmpty() && testNames.isEmpty()) {
            benchmark.testCases
        } else {
            benchmark.testCases.filter { it.name in testNames || it.file.toString() in fileNames }
        }
        val selectedTestCases = candidates.sample(samples ?: candidates.size, rnd)
        log.info { "Selected ${selectedTestCases.size} cases from ${candidates.size} candidates (out of ${benchmark.testCases.size} possible cases)." }
        if (selectedTestCases.isEmpty()) {
            log.warn { "No tests will be run!" }
        }

        for (testCase in ProgressBar.wrap(selectedTestCases, "Tests")) {
            val result = runTest(
                benchmark,
                testCaseDir,
                srcProjectDir,
                dstProjectDir,
                testCase,
                completeDeterministic,
                factory,
                quiet
            )
            results.add(result)
        }

        return BenchResultSet(name, results)
    }

    /**
     * Runs a single benchmark test.
     *
     * @param benchmark the benchmark being run
     * @param testCaseDir the directory with the benchmark test cases
     * @param srcProjectDir the directory with the project
     * @param dstProjectDir the directory with a copy of the project, which is modified for each test
     * @param testCase the test case being run
     * @param completeDeterministic whether to do deterministic completion
     * @param factory the term factory
     * @param quiet whether to perform a quiet run
     * @return the benchmark result
     */
    private fun runTest(
        benchmark: Benchmark,
        testCaseDir: Path,
        srcProjectDir: Path,
        dstProjectDir: Path,
        testCase: TestCase,
        completeDeterministic: Boolean,
        factory: ITermFactory,
        quiet: Boolean = false,
    ): BenchResult {
        if (!quiet) log.trace { "Preparing ${testCase.name}..." }
        // Copy the file to the temporary directory
        val srcInputFile = testCaseDir.resolve(testCase.inputFile)
        val dstInputFile = dstProjectDir.resolve(testCase.file)
        Files.createDirectories(dstInputFile.parent)
        Files.copy(srcInputFile, dstInputFile, StandardCopyOption.REPLACE_EXISTING)

        // Read the expected term
        val resExpectedFile = testCaseDir.resolve(testCase.expectedFile)
        val expectedTerm = StrategoTerms(factory).fromStratego(TAFTermReader(factory).readFromPath(resExpectedFile))

        if (!quiet) log.trace { "Running ${testCase.name} ${if (completeDeterministic) "with deterministic completion" else "no deterministic completion"}..." }
        val result = runBenchmarkTask.run(
            pie,
            benchmark,
            testCaseDir,
            srcProjectDir,
            dstProjectDir,
            testCase,
            expectedTerm,
            FSResource(dstInputFile).key,
            completeDeterministic,
        )
        if (!quiet) log.debug { "${testCase.name}: ${result.kind} (${result.timings.totalTime} ms)"}
        // Restore the file
        val origInputFile = srcProjectDir.resolve(testCase.file)
        Files.copy(origInputFile, dstInputFile, StandardCopyOption.REPLACE_EXISTING)
        return result
    }
}
