package mb.ccbench.webdsl

import mb.ccbench.BenchmarkRunner
import mb.pie.api.Pie
import mb.tego.strategies.runtime.TegoRuntime
import org.spoofax.interpreter.terms.ITermFactory
import javax.inject.Inject

class WebDSLBenchmarkRunner @Inject constructor(
    pie: Pie,
    runBenchmarkTask: WebDSLRunBenchmarkTask,
    termFactory: ITermFactory,
    tegoRuntime: TegoRuntime,
) : BenchmarkRunner(
    pie,
    runBenchmarkTask,
    termFactory,
    tegoRuntime,
)
