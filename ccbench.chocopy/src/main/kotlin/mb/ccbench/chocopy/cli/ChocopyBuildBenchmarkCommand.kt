package mb.ccbench.chocopy.cli

import mb.ccbench.cli.BuildBenchmarkCommand
import mb.ccbench.chocopy.ChocopyBenchmarkBuilder
import javax.inject.Inject

class ChocopyBuildBenchmarkCommand @Inject constructor(
    benchmarkBuilder: ChocopyBenchmarkBuilder
) : BuildBenchmarkCommand(
    ".tig",
    benchmarkBuilder
)
