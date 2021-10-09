package mb.codecompletion.bench.tiger.cli

import mb.codecompletion.bench.cli.RunBenchmarkCommand
import mb.codecompletion.bench.tiger.TigerBenchmarkRunner
import javax.inject.Inject

class TigerRunBenchmarkCommand @Inject constructor(
    benchmarkRunner: TigerBenchmarkRunner
) : RunBenchmarkCommand(
    benchmarkRunner
)
