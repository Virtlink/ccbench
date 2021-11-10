package mb.ccbench

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

/**
 * Runs a benchmark.
 */
abstract class BenchmarkRunner(
    private val pie: Pie,
    private val runBenchmarkTask: RunBenchmarkTask,
    private val strategoRuntimeProvider: Provider<StrategoRuntime>,
    private val tegoRuntime: TegoRuntime,
) {
    private val log = KotlinLogging.logger {}

    @OptIn(ExperimentalStdlibApi::class)
    fun run(
        name: String,
        benchmark: Benchmark,
        benchmarkFile: Path,
        projectDir: Path,
        tmpProjectDir: Path,
        outputDir: Path,
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
            samples,
            actualSeed,
            completeDeterministic,
            factory
        )
        log.debug { "Running tests..." }

        val dateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))

        log.debug { "Writing benchmark results..." }
        val resultsFile = outputDir.resolve("$dateString-$name.csv")
        Files.createDirectories(resultsFile.parent)
        BenchResultSet.writeToCsv(resultSet, resultsFile)
        log.info { "Wrote benchmark results to $resultsFile" }

        log.debug { "Creating benchmark summary..." }
        val summary = BenchmarkSummary.fromResults(
            actualSeed,
            completeDeterministic,
            resultSet,
            (tegoRuntime as? MeasuringTegoRuntime)?.measurements ?: emptyMap()
        )

        log.debug { "Writing benchmark summary..." }
        val summaryFile = outputDir.resolve("$dateString-$name.yml")
        Files.createDirectories(summaryFile.parent)
        BenchmarkSummary.write(summary, summaryFile)
        log.info { "Wrote benchmark summary to $summaryFile" }


        return resultSet
    }

    /**
     * Samples and runs a number of tests.
     *
     * @param benchmark the benchmark being run
     * @param testCaseDir the directory with the benchmark test cases
     * @param srcProjectDir the directory with the project
     * @param dstProjectDir the directory with a copy of the project, which is modified for each test
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
        val selectedTestCases = benchmark.testCases.sample(samples ?: benchmark.testCases.size, rnd)
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
        if (!quiet) log.info { "${testCase.name}: ${result.kind} (${result.timings.totalTime} ms)"}
        // Restore the file
        val origInputFile = srcProjectDir.resolve(testCase.file)
        Files.copy(origInputFile, dstInputFile, StandardCopyOption.REPLACE_EXISTING)
        return result
    }
}
