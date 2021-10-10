package mb.ccbench.webdsl.cli

import mb.ccbench.webdsl.WebDSLBenchmarkBuilder
import mb.ccbench.cli.BuildBenchmarkCommand
import javax.inject.Inject

class WebDSLBuildBenchmarkCommand @Inject constructor(
    benchmarkBuilder: WebDSLBenchmarkBuilder
) : BuildBenchmarkCommand(
    "app",
    benchmarkBuilder
)
