package mb.ccbench.tiger

import com.github.ajalt.mordant.terminal.Terminal
import mb.ccbench.BenchmarkRunner
import mb.ccbench.BenchmarkSummarizer
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
    summarizer: BenchmarkSummarizer,
) : BenchmarkRunner(
    pie,
    runBenchmarkTask,
    strategoRuntimeProvider,
    tegoRuntime,
    summarizer,
)
