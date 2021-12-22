package mb.ccbench.chocopy.cli

import mb.ccbench.cli.MainCommand
import mb.ccbench.cli.PlotBenchmarksCommand
import javax.inject.Inject

class ChocopyMainCommand @Inject constructor(
    buildBenchmarkCommand: ChocopyBuildBenchmarkCommand,
    runBenchmarkCommand: ChocopyRunBenchmarkCommand,
    plotBenchmarksCommand: PlotBenchmarksCommand,
): MainCommand(
    buildBenchmarkCommand,
    runBenchmarkCommand,
    plotBenchmarksCommand,
)
