package mb.ccbench.plot

import mb.ccbench.results.BenchResult
import mb.ccbench.results.BenchResultSet
import org.jfree.chart.ChartColor
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.XYDataItem
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.Color
import java.awt.Paint
import java.awt.geom.Ellipse2D


/**
 * Plots the result sets as a chart with the given time,
 * one line for each result set.
 *
 * @property projectY Function that projects the time out of the result.
 */
class TimePlotter(
    private val title: String,
    private val xAxisLabel: String,
    private val yAxisLabel: String,
    private val projectX: (BenchResult) -> Number,
    private val projectY: (BenchResult) -> Number,
) : MultiResultsPlotter {

    override fun plotToChart(resultSets: List<BenchResultSet>): JFreeChart {
        val dataset = buildDataset(resultSets)

        val chart = ChartFactory.createScatterPlot(
            title,          // chart title
            xAxisLabel,     // x axis label
            yAxisLabel,     // y axis label
            dataset,        // data
            PlotOrientation.VERTICAL,
            false,
            false,
            false,
        )

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.backgroundPaint = Color.white

        val plot = chart.xyPlot
        val renderer = plot.renderer
        for (i in 0 until dataset.seriesCount) {
            renderer.setSeriesShape(i, Ellipse2D.Double(0.0, 0.0, 2.0, 2.0))
            renderer.setSeriesPaint(i, paints[i % paints.size])
        }
        plot.backgroundPaint = Color.white
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.domainGridlinePaint = Color.GRAY
        plot.rangeGridlinePaint = Color.GRAY


//        val renderer = XYLineAndShapeRenderer()
//        //renderer.setSeriesLinesVisible(0, false);
//        //renderer.setSeriesShapesVisible(1, false);
//        plot.renderer = renderer

        // change the auto tick unit selection to integer units only...
        val rangeAxis = plot.rangeAxis as NumberAxis
        rangeAxis.standardTickUnits = NumberAxis.createIntegerTickUnits()

        return chart
    }

    /**
     * List of paints.
     */
    private val paints = listOf(
        ChartColor.DARK_BLUE,
        ChartColor.DARK_GREEN,
        ChartColor.DARK_RED,
        ChartColor.DARK_YELLOW,
        ChartColor.DARK_MAGENTA,
        ChartColor.DARK_CYAN,
        Color.DARK_GRAY,
        ChartColor.LIGHT_BLUE,
        ChartColor.LIGHT_GREEN,
        ChartColor.LIGHT_RED,
        ChartColor.LIGHT_YELLOW,
        ChartColor.LIGHT_MAGENTA,
        ChartColor.LIGHT_CYAN,
        Color.LIGHT_GRAY,
        ChartColor.VERY_DARK_BLUE,
        ChartColor.VERY_DARK_GREEN,
        ChartColor.VERY_DARK_RED,
        ChartColor.VERY_DARK_YELLOW,
        ChartColor.VERY_DARK_MAGENTA,
        ChartColor.VERY_DARK_CYAN,
        Color(0x55, 0x55, 0xFF),
        Color(0x55, 0xFF, 0x55),
        Color(0xFF, 0x55, 0x55),
        Color(0xFF, 0xFF, 0x55),
        Color(0xFF, 0x55, 0xFF),
        Color(0x55, 0xFF, 0xFF),
        Color.PINK,
        Color.GRAY,
        ChartColor.VERY_LIGHT_BLUE,
        ChartColor.VERY_LIGHT_GREEN,
        ChartColor.VERY_LIGHT_RED,
        ChartColor.VERY_LIGHT_YELLOW,
        ChartColor.VERY_LIGHT_MAGENTA,
        ChartColor.VERY_LIGHT_CYAN
    )

    /**
     * Builds a dataset from the given result sets.
     *
     * @param resultSets the result sets
     * @return the resulting data set
     */
    private fun buildDataset(resultSets: List<BenchResultSet>): XYDataset {
        val dataset = XYSeriesCollection()
        for (resultSet in resultSets){
            val series = buildXYSeries(resultSet)
            dataset.addSeries(series)
        }
        return dataset
    }

    /**
     * Builds an XY-series from the given result set.
     *
     * @param resultSet the result set
     * @return the XY-series
     */
    private fun buildXYSeries(resultSet: BenchResultSet): XYSeries {
        val series = XYSeries(resultSet.name)
        val items = resultSet.successResults.map {
            XYDataItem(projectX(it), projectY(it))
        }.sortedBy { it.xValue }
        for (item in items) {
            series.add(item)
        }
        return series
    }
}