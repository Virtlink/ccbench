package mb.ccbench.tiger.cli

import mb.ccbench.cli.RunBenchmarkCommand
import mb.ccbench.cli.VersionInfo
import mb.ccbench.tiger.TigerBenchmarkRunner
import javax.inject.Inject

class TigerRunBenchmarkCommand @Inject constructor(
    versionInfo: VersionInfo,
    benchmarkRunner: TigerBenchmarkRunner
) : RunBenchmarkCommand(
    versionInfo,
    benchmarkRunner,
)
