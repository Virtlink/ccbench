package mb.ccbench.webdsl.cli

import mb.ccbench.cli.MainCommand
import javax.inject.Inject

class WebDSLMainCommand @Inject constructor(
    prepareBenchmarkCommand: WebDSLBuildBenchmarkCommand,
    runBenchmarkCommand: WebDSLRunBenchmarkCommand,
): MainCommand(
    prepareBenchmarkCommand,
    runBenchmarkCommand,
)
