package mb.ccbench

import mb.ccbench.results.BenchResult
import mb.ccbench.results.BenchResultSet
import mb.ccbench.results.BenchmarkSummary
import mb.ccbench.utils.sample
import mb.nabl2.terms.stratego.StrategoTerms
import mb.pie.api.Pie
import mb.resource.fs.FSResource
import me.tongfei.progressbar.ProgressBar
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.spoofax.interpreter.terms.ITermFactory
import org.spoofax.terms.io.TAFTermReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*

/**
 * Runs a benchmark.
 */
abstract class BenchmarkRunner(
    private val pie: Pie,
    private val runBenchmarkTask: RunBenchmarkTask,
    private val termFactory: ITermFactory,
) {
    private val log = KotlinLogging.logger {}

    fun run(
        benchmark: Benchmark,
        benchmarkFile: Path,
        projectDir: Path,
        tmpProjectDir: Path,
        outputDir: Path,
        sample: Int?,
        seed: Long?,
        completeDeterministic: Boolean,
    ): BenchResultSet {
        val testCaseDir = benchmarkFile.parent.resolve(benchmark.testCaseDirectory)

        // Ensure the tmp project directory is empty, and copy the project
        FileUtils.deleteDirectory(tmpProjectDir.toFile())
        Files.createDirectories(tmpProjectDir.parent)
        Files.copy(projectDir, tmpProjectDir)

        val actualSeed = seed ?: System.nanoTime()
        val rnd = Random(actualSeed)

        // Run the tests
        val results = mutableListOf<BenchResult>()

        // Pick a random sample of test cases, or randomize the order
        val selectedTestCases = benchmark.testCases.sample(sample ?: benchmark.testCases.size, rnd)
        for (testCase in ProgressBar.wrap(selectedTestCases, "Tests")) {
            val result = runTest(benchmark, testCaseDir, projectDir, tmpProjectDir, testCase, completeDeterministic)
            results.add(result)
        }

        val resultSet = BenchResultSet(benchmark.name, results)

        log.trace { "Writing benchmark results..." }
        val resultsFile = outputDir.resolve("${benchmark.name}.csv")
        Files.createDirectories(resultsFile.parent)
        BenchResultSet.writeToCsv(resultSet, resultsFile)
        log.info { "Wrote benchmark results to $resultsFile" }

        log.trace { "Creating benchmark summary..." }
        val summary = BenchmarkSummary.fromResults(
            actualSeed,
            completeDeterministic,
            resultSet,
        )

        log.trace { "Writing benchmark summary..." }
        val summaryFile = outputDir.resolve("${benchmark.name}.yml")
        Files.createDirectories(summaryFile.parent)
        BenchmarkSummary.write(summary, summaryFile)
        log.info { "Wrote benchmark summary to $summaryFile" }
        return resultSet
    }

    fun runTest(
        benchmark: Benchmark,
        testCaseDir: Path,
        srcProjectDir: Path,
        dstProjectDir: Path,
        testCase: TestCase,
        completeDeterministic: Boolean,
    ): BenchResult {
        log.trace { "Preparing ${testCase.name}..." }
        // Copy the file to the temporary directory
        val srcInputFile = testCaseDir.resolve(testCase.inputFile)
        val dstInputFile = dstProjectDir.resolve(testCase.file)
        Files.createDirectories(dstInputFile.parent)
        Files.copy(srcInputFile, dstInputFile, StandardCopyOption.REPLACE_EXISTING)

        // Read the expected term
        val resExpectedFile = testCaseDir.resolve(testCase.expectedFile)
        val expectedTerm = StrategoTerms(termFactory).fromStratego(TAFTermReader(termFactory).readFromPath(resExpectedFile))

        log.trace { "Running ${testCase.name} ${if (completeDeterministic) "with deterministic completion" else "no deterministic completion"}..." }
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
        log.info { "${testCase.name}: ${result.kind} (${result.timings.totalTime} ms)"}
        // Restore the file
        val origInputFile = srcProjectDir.resolve(testCase.file)
        Files.copy(origInputFile, dstInputFile, StandardCopyOption.REPLACE_EXISTING)
        return result
    }
}
