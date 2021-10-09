package mb.codecompletion.bench.tiger

import mb.codecompletion.bench.BenchmarkBuilder
import mb.pie.api.Pie
import javax.inject.Inject

class TigerBenchmarkBuilder @Inject constructor(
    pie: Pie,
    prepareBenchmarkTask: TigerPrepareBenchmarkTask
) : BenchmarkBuilder(
    pie,
    prepareBenchmarkTask
)
