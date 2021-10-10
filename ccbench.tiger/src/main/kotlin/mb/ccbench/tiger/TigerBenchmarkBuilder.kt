package mb.ccbench.tiger

import mb.ccbench.BenchmarkBuilder
import mb.pie.api.Pie
import javax.inject.Inject

class TigerBenchmarkBuilder @Inject constructor(
    pie: Pie,
    prepareBenchmarkTask: TigerBuildBenchmarkTask
) : BenchmarkBuilder(
    pie,
    prepareBenchmarkTask
)
