package mb.ccbench.tiger

import mb.ccbench.BenchmarkRunner
import mb.pie.api.Pie
import mb.tego.strategies.runtime.TegoRuntime
import org.spoofax.interpreter.terms.ITermFactory
import javax.inject.Inject

class TigerBenchmarkRunner @Inject constructor(
    pie: Pie,
    runBenchmarkTask: TigerRunBenchmarkTask,
    termFactory: ITermFactory,
    tegoRuntime: TegoRuntime,
) : BenchmarkRunner(
    pie,
    runBenchmarkTask,
    termFactory,
    tegoRuntime
)
