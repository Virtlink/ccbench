package mb.codecompletion.plot

import mb.codecompletion.bench.results.BenchResultSet
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYDataItem
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.Color

/**
 * Plots the results.
 */
class ResultsPlotter {

    fun plot(resultSet: List<BenchResultSet>): JFreeChart {
        val dataset = buildDataset(resultSet)
        val chart = buildLineChart(dataset)

        return chart
    }

    private fun buildDataset(resultSet: List<BenchResultSet>): XYDataset {
        val dataset = XYSeriesCollection()
        for (result in resultSet){
            val series = buildXYSeries(result)
            dataset.addSeries(series)
        }
        return dataset
    }

    private fun buildXYSeries(resultSet: BenchResultSet): XYSeries {
        val series = XYSeries(resultSet.name)
        val items = resultSet.successResults.sortedBy { it.charSize }.map {
            XYDataItem(it.charSize, it.timings.totalTime)
        }
        for (item in items) {
            series.add(item)
        }
        return series
    }

    private fun buildLineChart(dataset: XYDataset): JFreeChart {
        // create the chart...
        val chart = ChartFactory.createXYLineChart(
            "Performance",  // chart title
            "X",            // x axis label
            "Y",            // y axis label
            dataset,        // data
            PlotOrientation.VERTICAL,
            true,           // include legend
            true,           // tooltips
            true            // urls
        )

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


        val renderer = XYLineAndShapeRenderer()
        //renderer.setSeriesLinesVisible(0, false);
        //renderer.setSeriesShapesVisible(1, false);
        plot.renderer = renderer

        // change the auto tick unit selection to integer units only...
        val rangeAxis = plot.rangeAxis as NumberAxis
        rangeAxis.standardTickUnits = NumberAxis.createIntegerTickUnits()
        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart
    }

}