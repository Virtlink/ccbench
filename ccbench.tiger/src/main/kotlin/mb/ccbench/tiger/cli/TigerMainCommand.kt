package mb.ccbench.tiger.cli

import mb.ccbench.cli.MainCommand
import javax.inject.Inject

class TigerMainCommand @Inject constructor(
    buildBenchmarkCommand: TigerBuildBenchmarkCommand,
    runBenchmarkCommand: TigerRunBenchmarkCommand,
): MainCommand(
    buildBenchmarkCommand,
    runBenchmarkCommand,
)
