package mb.ccbench.chocopy.cli

import mb.ccbench.cli.RunBenchmarkCommand
import mb.ccbench.chocopy.ChocopyBenchmarkRunner
import mb.ccbench.cli.VersionInfo
import javax.inject.Inject

class ChocopyRunBenchmarkCommand @Inject constructor(
    versionInfo: VersionInfo,
    benchmarkRunner: ChocopyBenchmarkRunner,
) : RunBenchmarkCommand(
    versionInfo,
    benchmarkRunner,
)
