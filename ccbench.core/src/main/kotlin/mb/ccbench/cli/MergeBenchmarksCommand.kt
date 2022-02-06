package mb.ccbench.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.path
import mb.ccbench.BenchmarkSummarizer
import mb.ccbench.results.BenchResultSet
import mb.ccbench.utils.withExtension
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject

/**
 * Merges benchmark results into one file, with summary.
 */
class MergeBenchmarksCommand @Inject constructor(
    private val versionInfo: VersionInfo,
    private val summarizer: BenchmarkSummarizer,
) : CliktCommand(name = "merge") {
    val inputFiles: List<Path> by option("-i", "--input", help = "Benchmark CSV results").path(mustExist = true, canBeFile = true, canBeDir = false).multiple(required = true)
    val outputFile: Path by option("-o", "--output", help = "Output file").path(mustExist = false, canBeFile = true, canBeDir = false).required()

    private val log = KotlinLogging.logger {}

    override fun run() {
        versionInfo.print()

        val actualOutputFile = outputFile.toAbsolutePath()

        log.debug { "Reading result sets..."}
        val resultSets = inputFiles.map { path -> BenchResultSet.readFromCsv(
            path.fileName.withExtension("").toString(),
            path.toAbsolutePath())
        }
        log.info { "Read ${resultSets.size} result sets."}

        log.debug { "Merging result sets..."}
        val mergedResultSet = BenchResultSet(
            "Merged",
            resultSets.flatMap { it.results }
        )
        log.info { "Merged ${resultSets.size} result sets."}

        val resultsFile = actualOutputFile.withExtension(".csv")
        val summaryFile = actualOutputFile.withExtension(".yml")

        log.debug { "Writing benchmark results..." }
        Files.createDirectories(resultsFile.parent)
        BenchResultSet.writeToCsv(mergedResultSet, resultsFile)
        log.info { "Wrote benchmark results to $resultsFile" }

        summarizer.summarize(
            mergedResultSet,
            summaryFile,
            null,
            null,
            null
        )

        log.info { "Done!" }
    }

}