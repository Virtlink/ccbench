package mb.ccbench.tiger.cli

import mb.ccbench.cli.BuildBenchmarkCommand
import mb.ccbench.cli.VersionInfo
import mb.ccbench.tiger.TigerBenchmarkBuilder
import javax.inject.Inject

class TigerBuildBenchmarkCommand @Inject constructor(
    versionInfo: VersionInfo,
    benchmarkBuilder: TigerBenchmarkBuilder,
) : BuildBenchmarkCommand(
    versionInfo,
    ".tig",
    benchmarkBuilder,
)
