package mb.ccbench.plot

import mb.ccbench.results.BenchResult
import mb.ccbench.results.BenchResultSet
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.util.ShapeUtils
import org.jfree.data.xy.XYDataItem
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.Color
import java.awt.Shape
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
        )

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.backgroundPaint = Color.white

        val plot = chart.xyPlot
        val renderer = plot.renderer
        renderer.setSeriesShape(0, Ellipse2D.Double(0.0, 0.0, 1.0, 1.0))
        plot.backgroundPaint = Color.lightGray
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.domainGridlinePaint = Color.white
        plot.rangeGridlinePaint = Color.white


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