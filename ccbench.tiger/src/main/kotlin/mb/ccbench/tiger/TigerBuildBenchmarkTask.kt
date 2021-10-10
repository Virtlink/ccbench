package mb.ccbench.tiger

import mb.ccbench.BuildBenchmarkTask
import mb.resource.text.TextResourceRegistry
import mb.tiger.task.*
import org.spoofax.interpreter.terms.ITermFactory
import org.spoofax.terms.io.SimpleTextTermWriter
import javax.inject.Inject

class TigerBuildBenchmarkTask @Inject constructor(
    parseTask: TigerParse,
    getSourceFilesTask: TigerGetSourceFiles,
    analyzeTask: TigerAnalyze,
//    analyzeTask: TigerAnalyzeMulti,
    explicateTask: TigerPreAnalyzeStatix,
    implicateTask: TigerPostAnalyzeStatix,
    upgradePlaceholdersTask: TigerUpgradePlaceholdersStatix,
    downgradePlaceholdersTask: TigerDowngradePlaceholdersStatix,
    prettyPrintTask: TigerPPPartial,
    textResourceRegistry: TextResourceRegistry,
    termFactory: ITermFactory,
    termWriter: SimpleTextTermWriter
) : BuildBenchmarkTask(
    parseTask,
    getSourceFilesTask,
    analyzeTask,
    explicateTask,
    implicateTask,
    upgradePlaceholdersTask,
    downgradePlaceholdersTask,
    prettyPrintTask,
    textResourceRegistry,
    termFactory,
    termWriter
)
