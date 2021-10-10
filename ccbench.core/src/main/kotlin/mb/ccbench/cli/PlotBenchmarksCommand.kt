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
        TimePlotter("Performance", "File size (characters)", "Total time (ms)", { it.charSize }, { it.timings.totalTime })
            .plot(resultSets, performancePdfFile, 400, 200)
        log.info { "Plotted performance chart to $performancePdfFile" }

        log.info { "Done!" }

    }

}