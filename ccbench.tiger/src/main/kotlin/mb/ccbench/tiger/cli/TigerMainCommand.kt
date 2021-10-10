package mb.ccbench.tiger.cli

import mb.ccbench.cli.MainCommand
import mb.ccbench.cli.PlotBenchmarksCommand
import javax.inject.Inject

class TigerMainCommand @Inject constructor(
    buildBenchmarkCommand: TigerBuildBenchmarkCommand,
    runBenchmarkCommand: TigerRunBenchmarkCommand,
    plotBenchmarksCommand: PlotBenchmarksCommand,
): MainCommand(
    buildBenchmarkCommand,
    runBenchmarkCommand,
    plotBenchmarksCommand,
)
