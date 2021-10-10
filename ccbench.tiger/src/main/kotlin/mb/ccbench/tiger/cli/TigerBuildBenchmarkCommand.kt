package mb.ccbench.tiger.cli

import mb.ccbench.cli.BuildBenchmarkCommand
import mb.ccbench.tiger.TigerBenchmarkBuilder
import javax.inject.Inject

class TigerBuildBenchmarkCommand @Inject constructor(
    benchmarkBuilder: TigerBenchmarkBuilder
) : BuildBenchmarkCommand(
    ".tig",
    benchmarkBuilder
)
