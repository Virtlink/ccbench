package mb.ccbench.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import mb.ccbench.plot.BoxPlotter
import mb.ccbench.plot.TimePlotter
import mb.ccbench.results.BenchResult
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
    val inputNames: List<String> by option("-n", "--name", help = "Name of the benchmark in the legend").multiple(required = false)
    val outputFile: Path by option("-o", "--output", help = "Output file").path(mustExist = false, canBeFile = true, canBeDir = false).required()
    val title: String? by option("-t", "--title", help = "Title of the plot")
    val yParameter: YParameter by option("-y", help = "Y-axis parameter to plot").enum<YParameter>().default(YParameter.Total)

    private val log = KotlinLogging.logger {}

    override fun run() {
        if (inputNames.isNotEmpty() && inputNames.size != inputFiles.size) {
            log.error { "Number of names does not match number of input files" }
            throw ProgramResult(2)
        }

        val actualTitle = if (title != null) title!! else "Performance"
        val actualOutputFile = outputFile.toAbsolutePath()

        log.debug { "Reading result sets..."}
        val resultSets = inputFiles.mapIndexed { i, path -> BenchResultSet.readFromCsv(
            if (inputNames.isNotEmpty()) inputNames[i] else path.fileName.withExtension("").toString(),
            path.toAbsolutePath())
        }
        log.info { "Read ${resultSets.size} result sets."}

        log.debug { "Plotting scatter plot..." }
        val scatterPdfFile = actualOutputFile.withExtension(".scatter.pdf")
        val scatterPngFile = actualOutputFile.withExtension(".scatter.png")
        TimePlotter(actualTitle, "File size (AST nodes)", yParameter.label, { it.astSize }, yParameter.projection)
            .plot(resultSets, scatterPdfFile, scatterPngFile, 800, 600)
        log.info { "Wrote scatter plot PDF to $scatterPdfFile" }
        log.info { "Wrote scatter plot PNG to $scatterPngFile" }

        log.debug { "Plotting box plots..." }
        for ((i, resultSet) in resultSets.withIndex()) {
            log.debug { "Plotting boc plot..." }
            val boxPdfFile = actualOutputFile.withExtension(".box$i.pdf")
            val boxPngFile = actualOutputFile.withExtension(".box$i.png")
            BoxPlotter(
                "$actualTitle (${resultSet.name})",
                "File size (AST nodes)",
                yParameter.label,
                { val bucket = (it.astSize / 200) * 200; bucket to ">= $bucket" },
                yParameter.projection)
                .plot(resultSet, boxPdfFile, boxPngFile, 800, 600)
            log.info { "Wrote box plot PDF to $boxPdfFile" }
            log.info { "Wrote box plot PNG to $boxPngFile" }
        }
        log.debug { "Wrote box plots." }

        log.info { "Done!" }

    }

    enum class YParameter(
        /** The label for the Y-axis. */
        val label: String,
        /** The projection function that maps a [BenchResult] to the value to be plotted. */
        val projection: (BenchResult) -> Number,
    ) {
        Parse("Parse time (ms)", { it.timings.parseTime }),
        Preparation("Parse time (ms)", { it.timings.preparationTime }),
        Analyze("Parse time (ms)", { it.timings.analyzeTime }),
        CodeCompletion("Code completion time (ms)", { it.timings.codeCompletionTime }),
        Finishing("Code completion time (ms)", { it.timings.finishingTime }),

        Total("Total time (ms)", { it.timings.totalTime }),
    }

}