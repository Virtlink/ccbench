package mb.ccbench.tiger.cli

import mb.ccbench.cli.RunBenchmarkCommand
import mb.ccbench.tiger.TigerBenchmarkRunner
import javax.inject.Inject

class TigerRunBenchmarkCommand @Inject constructor(
    benchmarkRunner: TigerBenchmarkRunner
) : RunBenchmarkCommand(
    benchmarkRunner
)
