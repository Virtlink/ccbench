package mb.ccbench.webdsl.cli

import mb.ccbench.cli.MainCommand
import mb.ccbench.cli.PlotBenchmarksCommand
import javax.inject.Inject

class WebDSLMainCommand @Inject constructor(
    buildBenchmarkCommand: WebDSLBuildBenchmarkCommand,
    runBenchmarkCommand: WebDSLRunBenchmarkCommand,
    plotBenchmarksCommand: PlotBenchmarksCommand,
): MainCommand(
    buildBenchmarkCommand,
    runBenchmarkCommand,
    plotBenchmarksCommand,
)
