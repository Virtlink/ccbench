package mb.ccbench.plot

import mb.ccbench.results.BenchResultSet
import org.jfree.chart.JFreeChart
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.outputStream

/**
 * Plots multiple result sets into a single chart.
 */
interface MultiResultsPlotter {

    /**
     * Plots the result sets and writes the resulting PDF and PNG to the specified files.
     *
     * @param resultSets the result sets to plot
     * @param pdfFile the file to write the plot PDF to
     * @param pngFile the file to write the plot PNG to
     * @param width the width of the plot
     * @param height the height of the plot
     */
    fun plot(resultSets: List<BenchResultSet>, pdfFile: Path, pngFile: Path, width: Int, height: Int) {
        pdfFile.outputStream().use { pdfOutputStream ->
            pngFile.outputStream().use { pngOutputStream ->
                plot(resultSets, pdfOutputStream, pngOutputStream, width, height)
            }
        }
    }

    /**
     * Plots the result sets and writes the resulting PDF and PNG to the specified streams.
     *
     * The stream is not closed by this method.
     *
     * @param resultSets the result sets to plot
     * @param pdfOutputStream the output stream to write the plot PDF to
     * @param pngOutputStream the output stream to write the plot PNG to
     * @param width the width of the plot
     * @param height the height of the plot
     */
    fun plot(resultSets: List<BenchResultSet>, pdfOutputStream: OutputStream, pngOutputStream: OutputStream, width: Int, height: Int) {
        val chart = plotToChart(resultSets)
        chart.writeToPdf(pdfOutputStream, width, height)
        chart.writeToPng(pngOutputStream, width, height)
    }

    /**
     * Plots the given result sets into a chart.
     *
     * @param resultSets the result sets to plot
     * @return the chart
     */
    fun plotToChart(resultSets: List<BenchResultSet>): JFreeChart

}