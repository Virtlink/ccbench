package mb.ccbench.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import mb.ccbench.plot.BoxPlotter
import mb.ccbench.plot.TimePlotter
import mb.ccbench.results.BenchResultSet
import mb.ccbench.utils.withExtension
import mu.KotlinLogging
import java.nio.file.Path
import javax.inject.Inject

/**
 * Plots benchmarks.
 */
class PlotBenchmarksCommand @Inject constructor() : CliktCommand(name = "plot") {
    val inputFiles: List<Path> by option("-i", "--input", help = "Benchmark CSV results").path(mustExist = true, canBeFile = true, canBeDir = false).multiple(required = true)
    val outputFile: Path by option("-o", "--output", help = "Output directory").path(mustExist = false, canBeFile = true, canBeDir = false).required()

    private val log = KotlinLogging.logger {}

    override fun run() {
        val actualOutputFile = outputFile.toAbsolutePath()

        log.debug { "Reading result sets..."}
        val resultSets = inputFiles.map { BenchResultSet.readFromCsv(it.fileName.withExtension("").toString(), it.toAbsolutePath()) }
        log.info { "Read ${resultSets.size} result sets."}

        log.debug { "Plotting scatter plot..." }
        val scatterPdfFile = actualOutputFile.withExtension(".scatter.pdf")
        val scatterPngFile = actualOutputFile.withExtension(".scatter.png")
        TimePlotter("Performance", "File size (AST nodes)", "Completion time (ms)", { it.astSize }, { it.timings.codeCompletionTime })
//        TimePlotter("Performance", "File size (AST nodes)", "Total time (ms)", { it.astSize }, { it.timings.totalTime })
            .plot(resultSets, scatterPdfFile, scatterPngFile, 800, 600)
        log.info { "Wrote scatter plot PDF to $scatterPdfFile" }
        log.info { "Wrote scatter plot PNG to $scatterPngFile" }

        log.debug { "Plotting box plots..." }
        for ((i, resultSet) in resultSets.withIndex()) {
            log.debug { "Plotting boc plot..." }
            val boxPdfFile = actualOutputFile.withExtension(".box$i.pdf")
            val boxPngFile = actualOutputFile.withExtension(".box$i.png")
            BoxPlotter(
                "Performance",
                "File size (AST nodes)",
                "Total time (ms)",
                { val bucket = (it.astSize / 200) * 200; bucket to ">= $bucket" },
                { it.timings.totalTime })
                .plot(resultSet, boxPdfFile, boxPngFile, 800, 600)
            log.info { "Wrote box plot PDF to $boxPdfFile" }
            log.info { "Wrote box plot PNG to $boxPngFile" }
        }
        log.debug { "Wrote box plots." }

        log.info { "Done!" }

    }

}