package mb.ccbench.chocopy.cli

import mb.ccbench.cli.MainCommand
import mb.ccbench.cli.MergeBenchmarksCommand
import mb.ccbench.cli.PlotBenchmarksCommand
import mb.ccbench.cli.SummarizeBenchmarkCommand
import javax.inject.Inject

class ChocopyMainCommand @Inject constructor(
    buildBenchmarkCommand: ChocopyBuildBenchmarkCommand,
    runBenchmarkCommand: ChocopyRunBenchmarkCommand,
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
