package mb.ccbench.results

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import mb.ccbench.format.PathSerializer
import mb.tego.strategies.runtime.MeasuringTegoRuntime
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter

/**
 * A summary of the benchmark results.
 */
data class BenchmarkSummary(
    val name: String,
    val seed: Long?,
    val deterministicCompletion: Boolean?,

    val totalFiles: Int,
    val totalTests: Int,
    val successTests: Int,
    val literalTests: Int,
    val failedTests: Int,
    val errorTests: Int,
    val noResultsTests: Int,
    val noPlaceholderTests: Int,
    val analysisFailedTests: Int,

    val mean: Timings,
    val min: Timings,
    val p01: Timings,
    val p05: Timings,
    val p10: Timings,
    val median: Timings,
    val p90: Timings,
    val p95: Timings,
    val p99: Timings,
    val max: Timings,

    val strategies: Map<String, Long>,
    val results: Map<String, Long>,
) {
    companion object {

        /**
         * Gets the benchmark summary.
         */
        fun fromResults(
            seed: Long?,
            deterministicCompletion: Boolean?,
            results: BenchResultSet,
            measurements: Map<String, MeasuringTegoRuntime.StrategyTime>?,
        ): BenchmarkSummary {
            val successResults = results.results.filter { it.kind == BenchResultKind.Success }

            val parseTimeStats = DescriptiveStatistics()
            val preparationTimeStats = DescriptiveStatistics()
            val analyzeTimeStats = DescriptiveStatistics()
            val codeCompletionTimeStats = DescriptiveStatistics()
            val finishingTimeStats = DescriptiveStatistics()
            val totalTimeStats = DescriptiveStatistics()

            val notCodeCompletionTimeStats = DescriptiveStatistics()

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

                notCodeCompletionTimeStats.addValue(result.timings.notCodeCompletionTime)

                expandRulesTimeStats.addValue(result.timings.expandRulesTime)
                expandInjectionsTimeStats.addValue(result.timings.expandInjectionsTime)
                expandQueriesTimeStats.addValue(result.timings.expandQueriesTime)
                expandDeterministicTimeStats.addValue(result.timings.expandDeterministicTime)
            }

            fun getTimings(f: (DescriptiveStatistics) -> Double): Timings =
                Timings(
                    f(parseTimeStats),
                    f(preparationTimeStats),
                    f(analyzeTimeStats),
                    f(codeCompletionTimeStats),
                    f(finishingTimeStats),
                    f(totalTimeStats),

                    f(notCodeCompletionTimeStats),

                    f(expandRulesTimeStats),
                    f(expandInjectionsTimeStats),
                    f(expandQueriesTimeStats),
                    f(expandDeterministicTimeStats),
                )

            val mean = getTimings { s -> s.mean }                   // MEAN(data)
            val min = getTimings { s -> s.min }                     // MIN(data)
            val p01 = getTimings { s -> s.getPercentile(01.0) }     // PERCENTILE.EXC(data, 0.01)
            val p05 = getTimings { s -> s.getPercentile(05.0) }     // PERCENTILE.EXC(data, 0.05)
            val p10 = getTimings { s -> s.getPercentile(10.0) }     // PERCENTILE.EXC(data, 0.10)
            val median = getTimings { s -> s.getPercentile(50.0) }  // MEDIAN(data)
            val p90 = getTimings { s -> s.getPercentile(90.0) }     // PERCENTILE.EXC(data, 0.90)
            val p95 = getTimings { s -> s.getPercentile(95.0) }     // PERCENTILE.EXC(data, 0.95)
            val p99 = getTimings { s -> s.getPercentile(99.0) }     // PERCENTILE.EXC(data, 0.99)
            val max = getTimings { s -> s.max }                     // MAX(data)

            val strategyTimes = measurements?.map { (k, v) -> k to v.strategyTime / 1000_000 }?.sortedByDescending { (_, v) -> v }?.toMap() ?: emptyMap()
            val resultTimes = measurements?.map { (k, v) -> k to v.resultTime / 1000_000 }?.sortedByDescending { (_, v) -> v }?.toMap() ?: emptyMap()

            return BenchmarkSummary(
                results.name,
                seed,
                deterministicCompletion,
                // @formatter:off
                totalFiles          = results.results.groupBy { it.originalFile }.size,
                totalTests          = results.results.size,
                successTests        = successResults.size,
                literalTests        = results.results.count { it.kind == BenchResultKind.Literal },
                failedTests         = results.results.count { it.kind == BenchResultKind.Failed },
                errorTests          = results.results.count { it.kind == BenchResultKind.Error },
                noResultsTests      = results.results.count { it.kind == BenchResultKind.NoResults },
                noPlaceholderTests  = results.results.count { it.kind == BenchResultKind.NoPlaceholder },
                analysisFailedTests = results.results.count { it.kind == BenchResultKind.AnalysisFailed },
                // @formatter:on
                mean,
                min, p01, p05, p10, median, p90, p95, p99, max,
                strategyTimes,
                resultTimes
            )
        }

        /**
         * Writes the summary to the specified writer.
         *
         * @param summary the summary to write
         * @param writer the writer to write to
         */
        fun write(summary: BenchmarkSummary, writer: Writer) {
            val mapper = createObjectMapper()
            mapper.writeValue(writer, summary)
        }

        /**
         * Writes the summary to the specified stream.
         *
         * @param summary the summary to write
         * @param stream the output stream to write to
         */
        fun write(summary: BenchmarkSummary, stream: OutputStream) =
            stream.bufferedWriter().use { writer -> write(summary, writer) }

        /**
         * Writes the summary to the specified file.
         *
         * @param summary the summary to write
         * @param file the file to write to
         */
        fun write(summary: BenchmarkSummary, file: Path) =
            file.bufferedWriter().use { writer -> write(summary, writer) }

        /**
         * Reads a summary from the specified reader.
         *
         * @param reader the reader to read from
         * @return the read summary
         */
        fun read(reader: Reader): BenchmarkSummary {
            val mapper = createObjectMapper()
            return mapper.readValue(reader, BenchmarkSummary::class.java)
        }

        /**
         * Reads a summary from the specified input stream.
         *
         * @param stream the input stream to read from
         * @return the read summary
         */
        fun read(stream: InputStream): BenchmarkSummary =
            stream.bufferedReader().use { reader -> read(reader) }

        /**
         * Reads a summary from the specified file.
         *
         * @param file the file to read from
         * @return the read summary
         */
        fun read(file: Path): BenchmarkSummary =
            file.bufferedReader().use { reader -> read(reader) }

        /**
         * Creates an object mapper for parsing YAML using Kotlin.
         *
         * @return the created object mapper
         */
        private fun createObjectMapper(): ObjectMapper {
            val mapper = ObjectMapper(YAMLFactory())
            val kotlinModule = KotlinModule.Builder().build()
            mapper.registerModule(kotlinModule)
            val customModule = SimpleModule()
                .addSerializer(Path::class.java, PathSerializer())
            mapper.registerModule(customModule)
            return mapper
        }
    }
}