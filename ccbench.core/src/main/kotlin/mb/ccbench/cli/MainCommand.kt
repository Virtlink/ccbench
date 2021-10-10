package mb.ccbench.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

abstract class MainCommand(
    vararg subcommands: CliktCommand
): CliktCommand() {
    init {
        subcommands(*subcommands)
    }

    override fun run() = Unit
}
