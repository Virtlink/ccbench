package mb.ccbench.tiger.cli

import mb.ccbench.cli.PrepareBenchmarkCommand
import mb.ccbench.tiger.TigerBenchmarkBuilder
import javax.inject.Inject

class TigerPrepareBenchmarkCommand @Inject constructor(
    benchmarkBuilder: TigerBenchmarkBuilder
) : PrepareBenchmarkCommand(
    ".tig",
    benchmarkBuilder
)
