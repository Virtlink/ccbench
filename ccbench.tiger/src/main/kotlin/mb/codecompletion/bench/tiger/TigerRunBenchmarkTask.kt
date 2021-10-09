package mb.codecompletion.bench.tiger

import mb.codecompletion.bench.RunBenchmarkTask
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
