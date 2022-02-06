package mb.ccbench.chocopy.cli

import mb.ccbench.cli.BuildBenchmarkCommand
import mb.ccbench.chocopy.ChocopyBenchmarkBuilder
import mb.ccbench.cli.VersionInfo
import javax.inject.Inject

class ChocopyBuildBenchmarkCommand @Inject constructor(
    versionInfo: VersionInfo,
    benchmarkBuilder: ChocopyBenchmarkBuilder,
) : BuildBenchmarkCommand(
    versionInfo,
    ".py",
    benchmarkBuilder
)
