package mb.ccbench.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import mb.ccbench.BenchmarkSummarizer
import mb.ccbench.plot.BoxPlotter
import mb.ccbench.plot.TimePlotter
import mb.ccbench.results.BenchResult
import mb.ccbench.results.BenchResultSet
import mb.ccbench.utils.withExtension
import mb.tego.strategies.runtime.MeasuringTegoRuntime
import mu.KotlinLogging
import java.nio.file.Path
import javax.inject.Inject

/**
 * Summarizes a benchmark.
 */
class SummarizeBenchmarkCommand @Inject constructor(
    private val summarizer: BenchmarkSummarizer,
) : CliktCommand(name = "summarize") {
    val inputFile: Path by option("-i", "--input", help = "Benchmark CSV results").path(mustExist = true, canBeFile = true, canBeDir = false).required()
    val outputFile: Path? by option("-o", "--output", help = "Output YML file").path(mustExist = false, canBeFile = true, canBeDir = false)

    private val log = KotlinLogging.logger {}

    override fun run() {
        val actualInputFile = inputFile.toAbsolutePath()
        val actualOutputFile = outputFile?.toAbsolutePath() ?: actualInputFile.withExtension(".yml")

        log.debug { "Reading result set..."}
        val resultSet = BenchResultSet.readFromCsv(
            "Input",
            actualInputFile
        )
        log.info { "Read result set."}

        summarizer.summarize(
            resultSet,
            actualOutputFile,
            null,
            null,
            null
        )

        log.info { "Done!" }

    }

}