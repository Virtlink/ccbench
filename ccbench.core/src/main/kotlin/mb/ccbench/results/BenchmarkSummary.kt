package mb.ccbench.results

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

/**
 * A summary of the benchmark results.
 */
data class BenchmarkSummary(
    val name: String,
    val testCount: Int,

    val mean: Timings,
    val p01: Timings,
    val p05: Timings,
    val p10: Timings,
    val median: Timings,
    val p90: Timings,
    val p95: Timings,
    val p99: Timings,
) {
    companion object {

        /**
         * Gets the benchmark summary.
         */
        fun fromResults(name: String, results: BenchResultSet): BenchmarkSummary {
            val successResults = results.results.filter { it.kind == BenchResultKind.Success }

            val parseTimeStats = DescriptiveStatistics()
            val preparationTimeStats = DescriptiveStatistics()
            val analyzeTimeStats = DescriptiveStatistics()
            val codeCompletionTimeStats = DescriptiveStatistics()
            val finishingTimeStats = DescriptiveStatistics()
            val totalTimeStats = DescriptiveStatistics()

            val expandRulesTimeStats = DescriptiveStatistics()
            val expandInjectionsTimeStats = DescriptiveStatistics()
            val expandQueriesTimeStats = DescriptiveStatistics()
            val expandDeterministicTimeStats = DescriptiveStatistics()

            for (result in successResults) {
                parseTimeStats.addValue(result.timings.parseTime)
                preparationTimeStats.addValue(result.timings.preparationTime)
                analyzeTimeStats.addValue(result.timings.analyzeTime)
                codeCompletionTimeStats.addValue(result.timings.codeCompletionTime)
                finishingTimeStats.addValue(result.timings.finishingTime)
                totalTimeStats.addValue(result.timings.totalTime)

                expandRulesTimeStats.addValue(result.timings.expandRulesTime)
                expandInjectionsTimeStats.addValue(result.timings.expandInjectionsTime)
                expandQueriesTimeStats.addValue(result.timings.expandQueriesTime)
                expandDeterministicTimeStats.addValue(result.timings.expandDeterministicTime)
            }

            fun getTimings(f: (DescriptiveStatistics) -> Double): Timings
                    = Timings(
                f(parseTimeStats),
                f(preparationTimeStats),
                f(analyzeTimeStats),
                f(codeCompletionTimeStats),
                f(finishingTimeStats),
                f(totalTimeStats),

                f(expandRulesTimeStats),
                f(expandInjectionsTimeStats),
                f(expandQueriesTimeStats),
                f(expandDeterministicTimeStats),
            )

            val mean = getTimings { s -> s.mean }                   // MEAN(data)
            val p01 = getTimings { s -> s.getPercentile(01.0) }     // PERCENTILE.EXC(data, 0.01)
            val p05 = getTimings { s -> s.getPercentile(05.0) }     // PERCENTILE.EXC(data, 0.05)
            val p10 = getTimings { s -> s.getPercentile(10.0) }     // PERCENTILE.EXC(data, 0.10)
            val median = getTimings { s -> s.getPercentile(50.0) }  // MEDIAN(data)
            val p90 = getTimings { s -> s.getPercentile(90.0) }     // PERCENTILE.EXC(data, 0.90)
            val p95 = getTimings { s -> s.getPercentile(95.0) }     // PERCENTILE.EXC(data, 0.95)
            val p99 = getTimings { s -> s.getPercentile(99.0) }     // PERCENTILE.EXC(data, 0.99)

            return BenchmarkSummary(
                name,
                successResults.size,
                mean,
                p01, p05, p10, median, p90, p95, p99
            )
        }
    }
}