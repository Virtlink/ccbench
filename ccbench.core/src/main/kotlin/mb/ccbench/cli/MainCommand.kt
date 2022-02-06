package mb.ccbench.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption
import mb.ccbench.cli.PropertiesManager.revision
import mb.ccbench.cli.PropertiesManager.version

abstract class MainCommand(
    vararg subcommands: CliktCommand
): CliktCommand() {
    init {
        versionOption("$version ($revision)") { PropertiesManager.versionInfo() } // TODO: Use VersionInfo object
        subcommands(*subcommands)
    }

    override fun run() = Unit
}
