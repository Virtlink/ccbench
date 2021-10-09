package mb.codecompletion.bench.tiger.di

import dagger.Component
import mb.codecompletion.bench.tiger.cli.TigerMainCommand
import mb.codecompletion.bench.di.BenchLoggerComponent
import mb.codecompletion.bench.di.BenchPlatformComponent
import mb.codecompletion.bench.di.BenchResourceServiceComponent
import mb.codecompletion.bench.di.BenchScope
import mb.pie.dagger.RootPieComponent
import mb.tiger.TigerResourcesComponent

@BenchScope
@Component(dependencies = [
    BenchLoggerComponent::class,
    TigerResourcesComponent::class,
    BenchResourceServiceComponent::class,
    BenchPlatformComponent::class,
    TigerBenchLanguageComponent::class,
    RootPieComponent::class,
])
interface TigerBenchComponent {
    val mainCommand: TigerMainCommand
}
