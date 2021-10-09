package mb.codecompletion.bench.tiger.cli

import mb.codecompletion.bench.cli.MainCommand
import javax.inject.Inject

class TigerMainCommand @Inject constructor(
    prepareBenchmarkCommand: TigerPrepareBenchmarkCommand,
    runBenchmarkCommand: TigerRunBenchmarkCommand,
): MainCommand(
    prepareBenchmarkCommand,
    runBenchmarkCommand,
)
