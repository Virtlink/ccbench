package mb.ccbench.tiger.di

import dagger.Component
import mb.ccbench.tiger.cli.TigerMainCommand
import mb.ccbench.di.BenchLoggerComponent
import mb.ccbench.di.BenchPlatformComponent
import mb.ccbench.di.BenchResourceServiceComponent
import mb.ccbench.di.BenchScope
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
