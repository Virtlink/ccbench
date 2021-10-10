package mb.ccbench.webdsl

import mb.ccbench.BuildBenchmarkTask
import mb.resource.text.TextResourceRegistry
import mb.webdsl.task.*
import org.spoofax.interpreter.terms.ITermFactory
import org.spoofax.terms.io.SimpleTextTermWriter
import javax.inject.Inject

class WebDSLBuildBenchmarkTask @Inject constructor(
    parseTask: WebDSLParse,
    getSourceFilesTask: WebDSLGetSourceFiles,
    analyzeTask: WebDSLAnalyze,
//    analyzeTask: WebDSLAnalyzeMulti,
    explicateTask: WebDSLPreAnalyzeStatix,
    implicateTask: WebDSLPostAnalyzeStatix,
    upgradePlaceholdersTask: WebDSLUpgradePlaceholdersStatix,
    downgradePlaceholdersTask: WebDSLDowngradePlaceholdersStatix,
    prettyPrintTask: WebDSLPPPartial,
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
