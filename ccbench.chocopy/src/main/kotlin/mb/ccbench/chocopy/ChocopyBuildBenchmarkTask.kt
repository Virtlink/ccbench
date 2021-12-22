package mb.ccbench.chocopy

import mb.ccbench.BuildBenchmarkTask
import mb.resource.text.TextResourceRegistry
import mb.stratego.common.StrategoRuntime
import mb.stratego.pie.GetStrategoRuntimeProvider
import mb.chocopy.task.*
import org.spoofax.interpreter.terms.ITermFactory
import org.spoofax.terms.io.SimpleTextTermWriter
import javax.inject.Inject
import javax.inject.Provider

class ChocopyBuildBenchmarkTask @Inject constructor(
    parseTask: ChocopyParse,
    strategoRuntimeProvider: Provider<StrategoRuntime>,
    getSourceFilesTask: ChocopyGetSourceFiles,
    analyzeTask: ChocopyAnalyze,
//    analyzeTask: ChocopyAnalyzeMulti,
    textResourceRegistry: TextResourceRegistry,
    termWriter: SimpleTextTermWriter
) : BuildBenchmarkTask(
    parseTask,
    strategoRuntimeProvider,
    getSourceFilesTask,
    analyzeTask,
    "pre-analyze",
    "post-analyze",
    "upgrade-placeholders",
    "downgrade-placeholders",
    "is-inj",
    "pp-partial",
    textResourceRegistry,
    termWriter
)
