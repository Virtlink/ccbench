package mb.ccbench.chocopy

import com.github.ajalt.mordant.terminal.Terminal
import mb.ccbench.BenchmarkRunner
import mb.pie.api.Pie
import mb.stratego.common.StrategoRuntime
import mb.tego.strategies.runtime.TegoRuntime
import org.spoofax.interpreter.terms.ITermFactory
import javax.inject.Inject
import javax.inject.Provider

class ChocopyBenchmarkRunner @Inject constructor(
    pie: Pie,
    runBenchmarkTask: ChocopyRunBenchmarkTask,
    strategoRuntimeProvider: Provider<StrategoRuntime>,
    tegoRuntime: TegoRuntime,
    terminal: Terminal,
) : BenchmarkRunner(
    pie,
    runBenchmarkTask,
    strategoRuntimeProvider,
    tegoRuntime,
    terminal,
)
