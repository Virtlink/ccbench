package mb.ccbench.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import mb.ccbench.plot.TimePlotter
import mb.ccbench.results.BenchResultSet
import mu.KotlinLogging
import java.nio.file.Path
import javax.inject.Inject

/**
 * Plots benchmarks.
 */
class PlotBenchmarksCommand @Inject constructor() : CliktCommand(name = "plot") {
    val name: String by argument(help = "Name of the benchmark")

    val inputFiles: List<Path> by option("-i", "--input", help = "Benchmark CSV results").path(mustExist = true, canBeFile = true, canBeDir = false).multiple(required = true)
    val outputDir: Path by option("-o", "--output", help = "Output directory").path(mustExist = false, canBeFile = false, canBeDir = true).required()

    private val log = KotlinLogging.logger {}

    override fun run() {
        val actualOutputDir = outputDir.toAbsolutePath()

        log.debug { "Reading result sets..."}
        val resultSets = inputFiles.map { BenchResultSet.readFromCsv(name, it.toAbsolutePath()) }
        log.info { "Read ${resultSets.size} result sets."}

        log.debug { "Plotting performance chart..." }
        val performancePdfFile = actualOutputDir.resolve("performance.pdf")
        val performancePngFile = actualOutputDir.resolve("performance.png")
        TimePlotter("Performance", "File size (AST nodes)", "Total time (ms)", { it.astSize }, { it.timings.totalTime })
            .plot(resultSets, performancePdfFile, performancePngFile, 800, 600)
        log.info { "Plotted performance chart PDF to $performancePdfFile" }
        log.info { "Plotted performance chart PNG to $performancePngFile" }

        log.info { "Done!" }

    }

}