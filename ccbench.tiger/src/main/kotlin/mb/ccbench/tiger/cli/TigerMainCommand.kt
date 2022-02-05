package mb.ccbench.tiger.cli

import mb.ccbench.cli.MainCommand
import mb.ccbench.cli.PlotBenchmarksCommand
import mb.ccbench.cli.SummarizeBenchmarkCommand
import javax.inject.Inject

class TigerMainCommand @Inject constructor(
    buildBenchmarkCommand: TigerBuildBenchmarkCommand,
    runBenchmarkCommand: TigerRunBenchmarkCommand,
    plotBenchmarksCommand: PlotBenchmarksCommand,
    summarizeBenchmarkCommand: SummarizeBenchmarkCommand,
): MainCommand(
    buildBenchmarkCommand,
    runBenchmarkCommand,
    plotBenchmarksCommand,
    summarizeBenchmarkCommand,
)
