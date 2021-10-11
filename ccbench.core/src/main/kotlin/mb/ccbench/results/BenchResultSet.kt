package mb.ccbench.results

import mb.ccbench.utils.NonClosingInputStream
import mb.ccbench.utils.NonClosingOutputStream
import mb.ccbench.utils.NonClosingReader
import mb.ccbench.utils.NonClosingWriter
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.*
import java.nio.file.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter

/**
 * A benchmark result set.
 *
 * @param name the name of the results (used as a label in the graphs)
 * @param results the individual results
 */
data class BenchResultSet(
    val name: String,
    val results: List<BenchResult>
) {

    /**
     * Gets a list of successful results.
     */
    val successResults get() = results.filter { it.success }

    companion object {
        private val csvFormat: CSVFormat = CSVFormat.Builder.create(CSVFormat.EXCEL)
            .setDelimiter(';')
            .setAutoFlush(true)
            .setHeader(*BenchResult.csvHeaders.toTypedArray())
            .build()

        /**
         * Reads the benchmark results as CSV from the specified reader.
         *
         * @param name the name of the result set
         * @param reader the reader to read from
         * @return the read benchmark results
         */
        fun readFromCsv(name: String, reader: Reader): BenchResultSet {
            val results = mutableListOf<BenchResult>()
            val bufferedReader = reader.buffered()
            val firstLine = bufferedReader.readLine()
            if (!firstLine.startsWith("sep="))
                throw IllegalStateException("Expected first line specifying separator, got: $firstLine")
            val headerLine = bufferedReader.readLine() // Discarded
            CSVParser(NonClosingReader(bufferedReader), csvFormat).use { parser ->
                for (record in parser) {
                    val result = BenchResult.fromCsvRecord(record.toList())
                    results.add(result)
                }
            }
            return BenchResultSet(name, results)
        }

        /**
         * Reads the benchmark results as CSV from the specified stream.
         *
         * @param name the name of the result set
         * @param stream the stream to read from
         * @return the read benchmark results
         */
        fun readFromCsv(name: String, stream: InputStream): BenchResultSet {
            NonClosingInputStream(stream).bufferedReader().use { reader ->
                return readFromCsv(name, reader)
            }
        }

        /**
         * Reads the benchmark results as CSV from the specified file.
         *
         * @param name the name of the result set
         * @param file the reader to read from
         * @return the read benchmark results
         */
        fun readFromCsv(name: String, file: Path): BenchResultSet {
            file.bufferedReader().use { reader ->
                return readFromCsv(name, reader)
            }
        }

        /**
         * Writes the benchmark results as CSV to the specified writer.
         *
         * This does not close the writer.
         *
         * @param resultSet the result set to write
         * @param writer the writer to write to
         */
        fun writeToCsv(resultSet: BenchResultSet, writer: Writer) {
            writer.write("sep=${csvFormat.delimiterString}\n")
            CSVPrinter(NonClosingWriter(writer), csvFormat).use { printer ->
                // Start with the successful results
                for (result in resultSet.results.filter { it.kind == BenchResultKind.Success }) {
                    printer.printRecord(*BenchResult.toCsvRecord(result).toTypedArray())
                }
                // Other kinds of results
                for (kind in BenchResultKind.values().filter { it != BenchResultKind.Success }) {
                    val resultsOfKind = resultSet.results.filter { it.kind == kind }
                    if (resultsOfKind.isNotEmpty()) {
                        for (result in resultsOfKind) {
                            printer.printRecord(*BenchResult.toCsvRecord(result).toTypedArray())
                        }
                    }
                }
            }
        }

        /**
         * Writes the benchmark results as CSV to the specified output stream.
         *
         * This does not close the stram.
         *
         * @param stream the output stream
         */
        fun writeToCsv(resultSet: BenchResultSet, stream: OutputStream) {
            NonClosingOutputStream(stream).bufferedWriter().use { writer ->
                writeToCsv(resultSet, writer)
            }
        }

        /**
         * Writes the benchmark results as CSV to the specified path.
         *
         * @param file the output file path
         */
        fun writeToCsv(resultSet: BenchResultSet, file: Path) {
            file.bufferedWriter().use { writer ->
                writeToCsv(resultSet, writer)
            }
        }
    }
}

/**
 * A single benchmark result.
 *
 * @property name The name of the benchmark test.
 * @property originalFile The original file being benchmarked.
 * @property kind The result of the benchmark.
 * @property charSize The size of the input file, in characters.
 * @property tokenSize The size of the input file, in tokens.
 * @property astSize The size of the input file, in AST nodes.
 * @property proposalCount The number of proposals.
 * @property timings The benchmark timings.
 */
data class BenchResult(
    val name: String,
    val originalFile: Path,
    val kind: BenchResultKind,
    val charSize: Long,
//    val lineSize: Long,
    val tokenSize: Long,
    val astSize: Long,
    val proposalCount: Int,
    val timings: Timings,
): Serializable {

    /** Whether the benchmark result indicates success. */
    val success: Boolean get() = kind == BenchResultKind.Success

    companion object {
        /** The CSV headers. */
        val csvHeaders = listOf(
            "Name",
            "OriginalFile",
            "Kind",
            "CharSize",
//            "LineSize",
            "TokenSize",
            "ASTSize",
            "NumberOfResults"
        ) + Timings.csvHeaders

        /**
         * Creates a CSV record from the object.
         *
         * @param obj the object
         * @return the list of CSV values
         */
        fun toCsvRecord(obj: BenchResult): List<String> = listOf(
            obj.name,
            obj.originalFile.toString(),
            obj.kind.toString(),
            obj.charSize.toString(),
//            obj.lineSize.toString(),
            obj.tokenSize.toString(),
            obj.astSize.toString(),
            obj.proposalCount.toString(),
        ) + Timings.toCsvRecord(obj.timings)

        /**
         * Creates an object from a CSV record.
         *
         * @param values the list of CSV values
         * @return the object
         */
        fun fromCsvRecord(values: List<String>, offset: Int = 0): BenchResult = BenchResult(
            values[offset + 0],
            Path.of(values[offset + 1]),
            BenchResultKind.valueOf(values[offset + 2]),
            values[offset + 3].toLong(),
//            values[offset + 4].toLong(),
            values[offset + 4].toLong(),
            values[offset + 5].toLong(),
            values[offset + 6].toInt(),
            Timings.fromCsvRecord(values, 7)
        )
    }
}
