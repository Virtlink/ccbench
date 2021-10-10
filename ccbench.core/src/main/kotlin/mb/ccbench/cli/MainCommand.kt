package mb.ccbench.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

abstract class MainCommand(
    buildBenchmarkCommand: BuildBenchmarkCommand,
    runBenchmarkCommand: RunBenchmarkCommand,
): CliktCommand() {
    init {
        subcommands(
            buildBenchmarkCommand,
            runBenchmarkCommand,
        )
    }

    override fun run() = Unit
}
