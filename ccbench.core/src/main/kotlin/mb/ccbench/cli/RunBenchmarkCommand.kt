package mb.ccbench.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import com.github.ajalt.clikt.parameters.types.path
import mb.ccbench.Benchmark
import mb.ccbench.BenchmarkRunner
import mb.ccbench.results.BenchResultSet
import mb.ccbench.utils.withExtension
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path

/**
 * Command that runs tests.
 */
abstract class RunBenchmarkCommand(
    private val versionInfo: VersionInfo,
    private val benchmarkRunner: BenchmarkRunner,
) : CliktCommand(name = "run") {
    val name: String? by argument(help = "Name of the benchmark").optional()

    val projectDir: Path by option("-p", "--project", help = "Project directory").path(mustExist = true, canBeFile = false, canBeDir = true).required()
    val inputFile: Path by option("-i", "--input", help = "Benchmark YAML file").path(mustExist = true, canBeFile = true, canBeDir = false).required()
    val outputDir: Path? by option("-o", "--output", help = "Output directory").path(mustExist = false, canBeFile = false, canBeDir = true)
    val fileNames: List<String> by option("-f", "--file", help = "The names of files whose tests are candidates; or none to allow all tests as candidates").multiple()
    val testNames: List<String> by option("-t", "--test", help = "The names of candidate tests; or none to allow all tests as candidates").multiple()
    val samples: Int? by option("-s", "--sample", help = "How many samples in total").int()
    val warmups: Int? by option("-w", "--warmups", help = "How many warmups in total").int()
    val seed: Long? by option("--seed", help = "The seed").long()
    val deterministic: Boolean by option("-d", "--deterministic", help = "Whether to run deterministic completion.").flag(default = false)

    private val log = KotlinLogging.logger {}

    override fun run() {
        versionInfo.print()

        val actualProjectDir = projectDir.toAbsolutePath()
        val actualInputFile = inputFile.toAbsolutePath()
        val actualOutputDir = (outputDir ?: Path.of("output/")).toAbsolutePath()
        val actualOutputFile = actualOutputDir.resolve(actualInputFile.withExtension(".csv").fileName).toAbsolutePath()
        val tmpProjectDir = actualOutputDir.resolve("tmp-project/").toAbsolutePath()
        log.debug("Project: $actualProjectDir")
        log.debug("Input file: $actualInputFile")
        log.debug("Output: $actualOutputDir")
        log.debug("Output file: $actualOutputFile")
        log.debug("Temp project dir: $tmpProjectDir")

        val benchmark = Benchmark.read(actualInputFile)
        benchmarkRunner.run(
            name ?: benchmark.name,
            benchmark,
            actualInputFile,
            actualProjectDir,
            tmpProjectDir,
            actualOutputDir,
            fileNames,
            testNames,
            samples,
            warmups,
            seed,
            deterministic,
        )
        log.info { "Done!" }
    }
}
