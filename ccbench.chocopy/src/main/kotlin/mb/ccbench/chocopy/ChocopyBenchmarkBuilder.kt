package mb.ccbench.chocopy

import mb.ccbench.BenchmarkBuilder
import mb.pie.api.Pie
import javax.inject.Inject

class ChocopyBenchmarkBuilder @Inject constructor(
    pie: Pie,
    prepareBenchmarkTask: ChocopyBuildBenchmarkTask
) : BenchmarkBuilder(
    pie,
    prepareBenchmarkTask
)
