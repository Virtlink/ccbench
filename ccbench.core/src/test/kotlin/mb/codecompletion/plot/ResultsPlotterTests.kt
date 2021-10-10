package mb.codecompletion.plot

import mb.codecompletion.bench.results.BenchResult
import mb.codecompletion.bench.results.BenchResultKind
import mb.codecompletion.bench.results.BenchResultSet
import mb.codecompletion.bench.results.Timings
import org.junit.jupiter.api.Test
import java.nio.file.Path

/**
 * Tests the [ResultsPlotter] class.
 */
class ResultsPlotterTests {

    @Test
    fun `plot to file`() {
        val resultSet1 = BenchResultSet(
            "Test Set 1",
            listOf(
                BenchResult("a1.txt", BenchResultKind.Success, 20, 10, 10, 5, Timings(10.0, 20.0, 30.0, 40.0, 50.0, 150.0, 0.0, 0.0, 0.0, 0.0)),
                BenchResult("b1.txt", BenchResultKind.Success, 20, 10, 10, 6, Timings(15.0, 25.0, 35.0, 45.0, 55.0, 175.0, 0.0, 0.0, 0.0, 0.0)),
                BenchResult("c1.txt", BenchResultKind.Success, 25, 12, 12, 7, Timings(20.0, 30.0, 40.0, 50.0, 60.0, 200.0, 0.0, 0.0, 0.0, 0.0)),
                BenchResult("d1.txt", BenchResultKind.Success, 30, 15, 15, 8, Timings(25.0, 35.0, 45.0, 55.0, 65.0, 225.0, 0.0, 0.0, 0.0, 0.0)),
            )
        )
        val resultSet2 = BenchResultSet(
            "Test Set 2",
            listOf(
                BenchResult("a2.txt", BenchResultKind.Success, 20, 10, 10, 5, Timings( 5.0, 15.0, 25.0, 35.0, 45.0, 125.0, 0.0, 0.0, 0.0, 0.0)),
                BenchResult("b2.txt", BenchResultKind.Success, 30, 12, 12, 7, Timings(10.0, 20.0, 30.0, 40.0, 50.0, 150.0, 0.0, 0.0, 0.0, 0.0)),
                BenchResult("c2.txt", BenchResultKind.Success, 40, 17, 17, 9, Timings(15.0, 25.0, 35.0, 45.0, 55.0, 175.0, 0.0, 0.0, 0.0, 0.0)),
                BenchResult("d2.txt", BenchResultKind.Success, 50, 21, 21, 9, Timings(20.0, 30.0, 40.0, 50.0, 60.0, 200.0, 0.0, 0.0, 0.0, 0.0)),
            )
        )
        val resultsPlotter = ResultsPlotter()

        val chart = resultsPlotter.plot(listOf(resultSet1, resultSet2))
        chart.writeToPdf(Path.of("test.pdf"), 500, 300)
    }

}