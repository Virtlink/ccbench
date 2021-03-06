package mb.ccbench.results

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

/**
 * The timing results.
 *
 * @property parseTime Time spent on parsing the input file with the placeholder; in ms.
 * @property preparationTime Time spent on preparing the input AST by explicating and upgrading it; in ms.
 * @property analyzeTime Time spent on analyzing the input AST with the placeholder; in ms.
 * @property codeCompletionTime Time spent on the code completion task; in ms.
 * @property finishingTime Time spent on finishing the code completion proposals, by downgrading, implicating, and pretty-printing them; in ms.
 * @property totalTime Time spent on the whole code completion, from parsing to finishing; in ms.
 *
 * @property notCodeCompletionTime Time _not_ spent on the code completion task; in ms.
 *
 * @property expandRulesTime Time spent on expanding rules; in ms.
 * @property expandInjectionsTime Time spent on expanding injections; in ms.
 * @property expandQueriesTime Time spent on expanding queries; in ms.
 * @property expandDeterministicTime Time spent on expanding deterministically; in ms.
 */
data class Timings(
    val parseTime: Double,
    val preparationTime: Double,
    val analyzeTime: Double,
    val codeCompletionTime: Double,
    val finishingTime: Double,
    val totalTime: Double,

    val notCodeCompletionTime: Double,

    val expandRulesTime: Double,
    val expandInjectionsTime: Double,
    val expandQueriesTime: Double,
    val expandDeterministicTime: Double,
) {
    companion object {
        /** The CSV headers. */
        val csvHeaders = listOf(
            "ParseTime",
            "PreparationTime",
            "AnalyzeTime",
            "CodeCompletionTime",
            "FinishingTime",
            "TotalTime",

            "NotCodeCompletionTime",

            "ExpandRulesTime",
            "ExpandInjectionsTime",
            "ExpandQueriesTime",
            "ExpandDeterministicTime",
        )

        val decimalFormatter: DecimalFormat = run {
            val otherSymbols = DecimalFormatSymbols(Locale.ROOT)
            otherSymbols.decimalSeparator = ','
            otherSymbols.groupingSeparator = '.'
            DecimalFormat("#0.00", otherSymbols)
        }

        /**
         * Creates a CSV record from the object.
         *
         * @param obj the object
         * @return the list of CSV values
         */
        fun toCsvRecord(obj: Timings): List<String> = listOf(
            decimalFormatter.format(obj.parseTime),               // ms
            decimalFormatter.format(obj.preparationTime),         // ms
            decimalFormatter.format(obj.analyzeTime),             // ms
            decimalFormatter.format(obj.codeCompletionTime),      // ms
            decimalFormatter.format(obj.finishingTime),           // ms
            decimalFormatter.format(obj.totalTime),               // ms

            decimalFormatter.format(obj.notCodeCompletionTime),   // ms

            decimalFormatter.format(obj.expandRulesTime),         // ms
            decimalFormatter.format(obj.expandInjectionsTime),    // ms
            decimalFormatter.format(obj.expandQueriesTime),       // ms
            decimalFormatter.format(obj.expandDeterministicTime), // ms
        )

        /**
         * Creates an object from a CSV record.
         *
         * @param values the list of CSV values
         * @return the object
         */
        fun fromCsvRecord(values: List<String>, offset: Int = 0): Timings {
            val parseTime: Double
            val preparationTime: Double
            val analyzeTime: Double
            val codeCompletionTime: Double
            val finishingTime: Double
            val totalTime: Double
            val notCodeCompletionTime: Double
            val expandRulesTime: Double
            val expandInjectionsTime: Double
            val expandQueriesTime: Double
            val expandDeterministicTime: Double
            if (values.size - offset == 11) {
                // @formatter:off
                parseTime               = decimalFormatter.parse(values[offset + 0]).toDouble()
                preparationTime         = decimalFormatter.parse(values[offset + 1]).toDouble()
                analyzeTime             = decimalFormatter.parse(values[offset + 2]).toDouble()
                codeCompletionTime      = decimalFormatter.parse(values[offset + 3]).toDouble()
                finishingTime           = decimalFormatter.parse(values[offset + 4]).toDouble()
                totalTime               = decimalFormatter.parse(values[offset + 5]).toDouble()
                notCodeCompletionTime   = decimalFormatter.parse(values[offset + 6]).toDouble()
                expandRulesTime         = decimalFormatter.parse(values[offset + 7]).toDouble()
                expandInjectionsTime    = decimalFormatter.parse(values[offset + 8]).toDouble()
                expandQueriesTime       = decimalFormatter.parse(values[offset + 9]).toDouble()
                expandDeterministicTime = decimalFormatter.parse(values[offset + 10]).toDouble()
                // @formatter:on
            } else if (values.size - offset == 10){
                // @formatter:off
                parseTime               = decimalFormatter.parse(values[offset + 0]).toDouble()
                preparationTime         = decimalFormatter.parse(values[offset + 1]).toDouble()
                analyzeTime             = decimalFormatter.parse(values[offset + 2]).toDouble()
                codeCompletionTime      = decimalFormatter.parse(values[offset + 3]).toDouble()
                finishingTime           = decimalFormatter.parse(values[offset + 4]).toDouble()
                totalTime               = decimalFormatter.parse(values[offset + 5]).toDouble()
                notCodeCompletionTime   = parseTime + preparationTime + analyzeTime + finishingTime
                expandRulesTime         = decimalFormatter.parse(values[offset + 6]).toDouble()
                expandInjectionsTime    = decimalFormatter.parse(values[offset + 7]).toDouble()
                expandQueriesTime       = decimalFormatter.parse(values[offset + 8]).toDouble()
                expandDeterministicTime = decimalFormatter.parse(values[offset + 9]).toDouble()
                // @formatter:on
            } else {
                throw IllegalArgumentException("Invalid number of values: ${values.size}")
            }
            return Timings(
                parseTime,
                preparationTime,
                analyzeTime,
                codeCompletionTime,
                finishingTime,
                totalTime,

                notCodeCompletionTime,

                expandRulesTime,
                expandInjectionsTime,
                expandQueriesTime,
                expandDeterministicTime,
            )
        }
    }
}