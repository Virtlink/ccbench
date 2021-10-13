package mb.ccbench.plot

import mb.ccbench.results.BenchResultSet
import org.jfree.chart.JFreeChart
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.outputStream

/**
 * Plots a single result set into a single chart.
 */
interface SingleResultPlotter {

    /**
     * Plots the result set and writes the resulting PDF and PNG to the specified files.
     *
     * @param resultSet the result set to plot
     * @param pdfFile the file to write the plot PDF to
     * @param pngFile the file to write the plot PNG to
     * @param width the width of the plot
     * @param height the height of the plot
     */
    fun plot(resultSet: BenchResultSet, pdfFile: Path, pngFile: Path, width: Int, height: Int) {
        pdfFile.outputStream().use { pdfOutputStream ->
            pngFile.outputStream().use { pngOutputStream ->
                plot(resultSet, pdfOutputStream, pngOutputStream, width, height)
            }
        }
    }

    /**
     * Plots the result set and writes the resulting PDF and PNG to the specified streams.
     *
     * The stream is not closed by this method.
     *
     * @param resultSet the result set to plot
     * @param pdfOutputStream the output stream to write the plot PDF to
     * @param pngOutputStream the output stream to write the plot PNG to
     * @param width the width of the plot
     * @param height the height of the plot
     */
    fun plot(resultSet: BenchResultSet, pdfOutputStream: OutputStream, pngOutputStream: OutputStream, width: Int, height: Int) {
        val chart = plotToChart(resultSet)
        chart.writeToPdf(pdfOutputStream, width, height)
        chart.writeToPng(pngOutputStream, width, height)
    }

    /**
     * Plots the given result set into a chart.
     *
     * @param resultSet the result set to plot
     * @return the chart
     */
    fun plotToChart(resultSet: BenchResultSet): JFreeChart

}