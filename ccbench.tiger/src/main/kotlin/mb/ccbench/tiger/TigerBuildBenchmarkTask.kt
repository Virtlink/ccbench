package mb.ccbench.tiger

import mb.ccbench.BuildBenchmarkTask
import mb.resource.text.TextResourceRegistry
import mb.stratego.common.StrategoRuntime
import mb.stratego.pie.GetStrategoRuntimeProvider
import mb.tiger.task.*
import org.spoofax.interpreter.terms.ITermFactory
import org.spoofax.terms.io.SimpleTextTermWriter
import javax.inject.Inject
import javax.inject.Provider

class TigerBuildBenchmarkTask @Inject constructor(
    parseTask: TigerParse,
    strategoRuntimeProvider: Provider<StrategoRuntime>,
    getSourceFilesTask: TigerGetSourceFiles,
    analyzeTask: TigerAnalyze,
//    analyzeTask: TigerAnalyzeMulti,
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
    "downgrade-placeholders-tiger-Module", // "downgrade-placeholders",
    "is-inj",
    "pp-partial",
    textResourceRegistry,
    termWriter
)
