package mb.codecompletion.plot

import mb.codecompletion.bench.BenchmarkResults
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.Color
import java.io.OutputStream


//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;

/**
 * Plots the results.
 */
class ResultsPlotter {

    fun plot(results: BenchmarkResults): JFreeChart {
        // create the chart...

        val dataset = buildDataset(results)
        val chart = buildLineChart(dataset)

        return chart
    }

    private fun buildDataset(results: BenchmarkResults): XYDataset {
        val series1 = XYSeries("First")
        series1.add(1.0, 1.0)
        series1.add(2.0, 4.0)
        series1.add(3.0, 3.0)
        series1.add(4.0, 5.0)
        series1.add(5.0, 5.0)
        series1.add(6.0, 7.0)
        series1.add(7.0, 7.0)
        series1.add(8.0, 8.0)
        val series2 = XYSeries("Second")
        series2.add(1.0, 5.0)
        series2.add(2.0, 7.0)
        series2.add(3.0, 6.0)
        series2.add(4.0, 8.0)
        series2.add(5.0, 4.0)
        series2.add(6.0, 4.0)
        series2.add(7.0, 2.0)
        series2.add(8.0, 1.0)
        val series3 = XYSeries("Third")
        series3.add(3.0, 4.0)
        series3.add(4.0, 3.0)
        series3.add(5.0, 2.0)
        series3.add(6.0, 3.0)
        series3.add(7.0, 6.0)
        series3.add(8.0, 3.0)
        series3.add(9.0, 4.0)
        series3.add(10.0, 3.0)
        val dataset = XYSeriesCollection()
        dataset.addSeries(series1)
        dataset.addSeries(series2)
        dataset.addSeries(series3)
        return dataset
    }

    fun buildLineChart(dataset: XYDataset): JFreeChart {
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