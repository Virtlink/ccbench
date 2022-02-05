package mb.ccbench.tiger.cli

import mb.ccbench.cli.MainCommand
import mb.ccbench.cli.MergeBenchmarksCommand
import mb.ccbench.cli.PlotBenchmarksCommand
import mb.ccbench.cli.SummarizeBenchmarkCommand
import javax.inject.Inject

class TigerMainCommand @Inject constructor(
    buildBenchmarkCommand: TigerBuildBenchmarkCommand,
    runBenchmarkCommand: TigerRunBenchmarkCommand,
    plotBenchmarksCommand: PlotBenchmarksCommand,
    summarizeBenchmarkCommand: SummarizeBenchmarkCommand,
    mergeBenchmarksCommand: MergeBenchmarksCommand,
): MainCommand(
    buildBenchmarkCommand,
    runBenchmarkCommand,
    plotBenchmarksCommand,
    summarizeBenchmarkCommand,
    mergeBenchmarksCommand,
)
