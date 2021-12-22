package mb.ccbench.chocopy

import mb.ccbench.RunBenchmarkTask
import mb.chocopy.task.ChocopyCodeCompletionTaskDef
import mb.chocopy.task.ChocopyParse
import javax.inject.Inject

class ChocopyRunBenchmarkTask @Inject constructor(
    parseTask: ChocopyParse,
    codeCompletionTask: ChocopyCodeCompletionTaskDef
) : RunBenchmarkTask(
    parseTask,
    codeCompletionTask
)
