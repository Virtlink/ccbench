package mb.ccbench.tiger

import mb.ccbench.RunBenchmarkTask
import mb.tiger.task.TigerCodeCompletionTaskDef
import mb.tiger.task.TigerParse
import javax.inject.Inject

class TigerRunBenchmarkTask @Inject constructor(
    parseTask: TigerParse,
    codeCompletionTask: TigerCodeCompletionTaskDef
) : RunBenchmarkTask(
    parseTask,
    codeCompletionTask
)
