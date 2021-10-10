package mb.ccbench.plot

import mb.ccbench.results.BenchResult
import mb.ccbench.results.BenchResultSet
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.data.xy.XYDataItem
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.Color

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
//        // create the chart...
//        val chart = ChartFactory.createXYLineChart(
//            title,          // chart title
//            xAxisLabel,     // x axis label
//            yAxisLabel,     // y axis label
//            dataset,        // data
//            PlotOrientation.VERTICAL,
//            true,           // include legend
//            true,           // tooltips
//            true            // urls
//        )

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.backgroundPaint = Color.white

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        val plot = chart.xyPlot
        plot.backgroundPaint = Color.lightGray
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.domainGridlinePaint = Color.white
        plot.rangeGridlinePaint = Color.white


//        val renderer = XYLineAndShapeRenderer()
//        //renderer.setSeriesLinesVisible(0, false);
//        //renderer.setSeriesShapesVisible(1, false);
//        plot.renderer = renderer
//
//        // change the auto tick unit selection to integer units only...
//        val rangeAxis = plot.rangeAxis as NumberAxis
//        rangeAxis.standardTickUnits = NumberAxis.createIntegerTickUnits()
        // OPTIONAL CUSTOMISATION COMPLETED.
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