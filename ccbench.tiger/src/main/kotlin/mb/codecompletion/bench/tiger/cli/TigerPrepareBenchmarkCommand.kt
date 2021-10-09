package mb.codecompletion.bench.tiger.cli

import mb.codecompletion.bench.cli.PrepareBenchmarkCommand
import mb.codecompletion.bench.tiger.TigerBenchmarkBuilder
import javax.inject.Inject

class TigerPrepareBenchmarkCommand @Inject constructor(
    benchmarkBuilder: TigerBenchmarkBuilder
) : PrepareBenchmarkCommand(
    benchmarkBuilder
)
