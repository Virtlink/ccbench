package mb.ccbench

import com.github.ajalt.mordant.rendering.BorderStyle
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.SectionBuilder
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import mb.ccbench.results.BenchResultSet
import mb.ccbench.results.BenchmarkSummary
import mb.ccbench.results.Timings
import mb.tego.strategies.runtime.MeasuringTegoRuntime
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

/**
 * Summarizes benchmarks.
 */
class BenchmarkSummarizer @Inject constructor(
    private val terminal: Terminal,
) {
    private val log = KotlinLogging.logger {}

    fun summarize(
        resultSet: BenchResultSet,
        summaryFile: Path,
        measurements: Map<String, MeasuringTegoRuntime.StrategyTime>?,
        seed: Long?,
        completeDeterministic: Boolean?
    ) {
        log.debug { "Creating benchmark summary..." }
        val summary = BenchmarkSummary.fromResults(
            seed,
            completeDeterministic,
            resultSet,
            measurements ?: emptyMap()
        )

        log.debug { "Writing benchmark summary..." }
        Files.createDirectories(summaryFile.parent)
        BenchmarkSummary.write(summary, summaryFile)
        log.info { "Wrote benchmark summary to $summaryFile" }

        fun SectionBuilder.summaryRow(name: String, f: (Timings) -> Double) {
            row(
                name,
                Timings.decimalFormatter.format(f(summary.min)),
                Timings.decimalFormatter.format(f(summary.p01)),
                Timings.decimalFormatter.format(f(summary.p05)),
                Timings.decimalFormatter.format(f(summary.median)),
                Timings.decimalFormatter.format(f(summary.p95)),
                Timings.decimalFormatter.format(f(summary.p99)),
                Timings.decimalFormatter.format(f(summary.max)),
            )
        }

        terminal.println(table {
            borderStyle = BorderStyle.SQUARE_DOUBLE_SECTION_SEPARATOR
            align = TextAlign.RIGHT
            outerBorder = false
            column(0) {
                align = TextAlign.LEFT
                borders = Borders.ALL
                style = TextColors.magenta
            }
            header {
                style(TextColors.magenta, bold = true)
                row("", "Min", "P01", "P05", "P50", "P95", "P99", "Max")
            }
            body {
                rowStyles(TextColors.blue, TextColors.brightBlue)
                borders = Borders.TOM_BOTTOM
                summaryRow("Parsing") { t -> t.parseTime }
                summaryRow("Preparation") { t -> t.preparationTime }
                summaryRow("Analysis") { t -> t.analyzeTime }
                summaryRow("Code Completion") { t -> t.codeCompletionTime }
                summaryRow("Finishing") { t -> t.finishingTime }
            }
            footer {
                style(bold = true)
                summaryRow("Total") { t -> t.totalTime }
            }
        })
    }
}