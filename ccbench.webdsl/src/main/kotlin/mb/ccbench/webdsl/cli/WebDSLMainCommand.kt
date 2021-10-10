package mb.ccbench.webdsl.cli

import mb.ccbench.cli.MainCommand
import javax.inject.Inject

class WebDSLMainCommand @Inject constructor(
    buildBenchmarkCommand: WebDSLBuildBenchmarkCommand,
    runBenchmarkCommand: WebDSLRunBenchmarkCommand,
): MainCommand(
    buildBenchmarkCommand,
    runBenchmarkCommand,
)
