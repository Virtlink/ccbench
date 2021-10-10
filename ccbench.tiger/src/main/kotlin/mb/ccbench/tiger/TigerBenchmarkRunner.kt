package mb.ccbench.tiger

import mb.ccbench.BenchmarkRunner
import mb.pie.api.Pie
import org.spoofax.interpreter.terms.ITermFactory
import javax.inject.Inject

class TigerBenchmarkRunner @Inject constructor(
    pie: Pie,
    runBenchmarkTask: TigerRunBenchmarkTask,
    termFactory: ITermFactory
) : BenchmarkRunner(
    pie,
    runBenchmarkTask,
    termFactory
)
