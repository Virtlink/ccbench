package mb.ccbench.webdsl.cli

import mb.ccbench.cli.RunBenchmarkCommand
import mb.ccbench.webdsl.WebDSLBenchmarkRunner
import javax.inject.Inject

class WebDSLRunBenchmarkCommand @Inject constructor(
    benchmarkRunner: WebDSLBenchmarkRunner
) : RunBenchmarkCommand(
    benchmarkRunner
)
