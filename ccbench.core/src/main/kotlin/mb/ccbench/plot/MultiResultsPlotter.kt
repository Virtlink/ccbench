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
     * Plots the result sets and writes the resulting PDF to the specified file.
     *
     * @param resultSets the result sets to plot
     * @param file the file to write the plot PDF to
     * @param width the width of the plot
     * @param height the height of the plot
     */
    fun plot(resultSets: List<BenchResultSet>, file: Path, width: Int, height: Int) {
        file.outputStream().use { outputStream ->
            plot(resultSets, outputStream, width, height)
        }
    }

    /**
     * Plots the result sets and writes the resulting PDF to the specified file.
     *
     * The stream is not closed by this method.
     *
     * @param resultSets the result sets to plot
     * @param stream the output stream to write the plot PDF to
     * @param width the width of the plot
     * @param height the height of the plot
     */
    fun plot(resultSets: List<BenchResultSet>, stream: OutputStream, width: Int, height: Int) {
        val chart = plotToChart(resultSets)
        chart.writeToPdf(stream, width, height)
    }

    /**
     * Plots the given result sets into a chart.
     *
     * @param resultSets the result sets to plot
     * @return the chart
     */
    fun plotToChart(resultSets: List<BenchResultSet>): JFreeChart

}