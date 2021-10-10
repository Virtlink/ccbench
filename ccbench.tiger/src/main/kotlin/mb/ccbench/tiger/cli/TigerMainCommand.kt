package mb.ccbench.tiger.cli

import mb.ccbench.cli.MainCommand
import javax.inject.Inject

class TigerMainCommand @Inject constructor(
    prepareBenchmarkCommand: TigerBuildBenchmarkCommand,
    runBenchmarkCommand: TigerRunBenchmarkCommand,
): MainCommand(
    prepareBenchmarkCommand,
    runBenchmarkCommand,
)
