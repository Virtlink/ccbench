package mb.ccbench.chocopy.cli

import mb.ccbench.cli.RunBenchmarkCommand
import mb.ccbench.chocopy.ChocopyBenchmarkRunner
import javax.inject.Inject

class ChocopyRunBenchmarkCommand @Inject constructor(
    benchmarkRunner: ChocopyBenchmarkRunner
) : RunBenchmarkCommand(
    benchmarkRunner
)
