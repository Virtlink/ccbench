package mb.ccbench.tiger

import mb.ccbench.BenchmarkRunner
import mb.pie.api.Pie
import mb.stratego.common.StrategoRuntime
import mb.tego.strategies.runtime.TegoRuntime
import org.spoofax.interpreter.terms.ITermFactory
import javax.inject.Inject
import javax.inject.Provider

class TigerBenchmarkRunner @Inject constructor(
    pie: Pie,
    runBenchmarkTask: TigerRunBenchmarkTask,
    strategoRuntimeProvider: Provider<StrategoRuntime>,
    tegoRuntime: TegoRuntime,
) : BenchmarkRunner(
    pie,
    runBenchmarkTask,
    strategoRuntimeProvider,
    tegoRuntime
)
