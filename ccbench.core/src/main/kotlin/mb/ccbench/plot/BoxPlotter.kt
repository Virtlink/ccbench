package mb.ccbench.plot

import mb.ccbench.results.BenchResult
import mb.ccbench.results.BenchResultSet
import org.jfree.chart.ChartColor
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.CategoryAxis
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.DatasetRenderingOrder
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer
import org.jfree.chart.renderer.category.LineAndShapeRenderer
import org.jfree.data.category.DefaultCategoryDataset
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset
import java.awt.Color
import java.util.*


class BoxPlotter(
    private val title: String,
    private val xAxisLabel: String,
    private val yAxisLabel: String,
    private val projectGroup: (BenchResult) -> Pair<Long, String>,
    private val projectValue: (BenchResult) -> Number,
) : SingleResultPlotter {
    override fun plotToChart(resultSet: BenchResultSet): JFreeChart {

        val data = DefaultBoxAndWhiskerCategoryDataset()
        resultSet.successResults
            .groupBy { projectGroup(it) }
            .toSortedMap { o1, o2 -> o1.first.compareTo(o2.first) }
            .map { it.key.second to it.value.map { v -> projectValue(v) } }
            .forEach { (k, vs) -> data.add(vs, xAxisLabel, k) }

        val chart = ChartFactory.createBoxAndWhiskerChart(
            title,
            xAxisLabel,
            yAxisLabel,
            data,
            true
        )

        chart.backgroundPaint = Color.white
        val plot = chart.categoryPlot
        plot.backgroundPaint = Color.white
        plot.domainGridlinePaint = Color.GRAY
        plot.rangeGridlinePaint = Color.GRAY
        plot.orientation = PlotOrientation.HORIZONTAL
        val renderer = plot.renderer as BoxAndWhiskerRenderer
        renderer.isMeanVisible = false
        renderer.isMaxOutlierVisible = true
        renderer.setSeriesFillPaint(0, ChartColor.WHITE)
//        renderer.setSeriesPaint()
        renderer.setSeriesOutlinePaint(0, ChartColor.BLACK)


        return chart
    }
}