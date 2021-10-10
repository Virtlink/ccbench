package mb.ccbench.tiger

import mb.ccbench.di.BenchLoggerModule
import mb.ccbench.di.DaggerBenchLoggerComponent
import mb.ccbench.di.DaggerBenchPlatformComponent
import mb.ccbench.di.DaggerBenchResourceServiceComponent
import mb.ccbench.tiger.di.DaggerTigerBenchComponent
import mb.ccbench.tiger.di.DaggerTigerBenchLanguageComponent
import mb.ccbench.tiger.di.TigerBenchModule
import mb.pie.dagger.DaggerRootPieComponent
import mb.pie.dagger.RootPieModule
import mb.pie.runtime.PieBuilderImpl
import mb.resource.dagger.RootResourceServiceModule
import mb.resource.text.TextResourceRegistry
import mb.tiger.DaggerTigerResourcesComponent
import mb.tiger.TigerModule

fun main(args: Array<String>) {

    // Platform:
    val loggerComponent = DaggerBenchLoggerComponent.builder()
        .benchLoggerModule(BenchLoggerModule())
        .build()

    // Language:
    val resourcesComponent = DaggerTigerResourcesComponent.create()
    // Platform:
    val textResourceRegistry = TextResourceRegistry()
    val resourceServiceModule = RootResourceServiceModule()
        .addRegistriesFrom(resourcesComponent)
        .addRegistry(textResourceRegistry)
    val resourceServiceComponent = DaggerBenchResourceServiceComponent.builder()
        .benchLoggerComponent(loggerComponent)
        .rootResourceServiceModule(resourceServiceModule)
        .build()
    val platformComponent = DaggerBenchPlatformComponent.builder()
        .benchLoggerComponent(loggerComponent)
        .benchResourceServiceComponent(resourceServiceComponent)
        .build()

    // Language:
    val languageComponent = mb.ccbench.tiger.di.DaggerTigerBenchLanguageComponent.builder()
        .tigerModule(TigerModule())
        .tigerBenchModule(TigerBenchModule(textResourceRegistry))
        .benchLoggerComponent(loggerComponent)
        .resourceServiceComponent(resourceServiceComponent)
        .benchPlatformComponent(platformComponent)
        .tigerResourcesComponent(resourcesComponent)
        .build()

    // PIE
    val pieModule = RootPieModule({ PieBuilderImpl() }, languageComponent)
        //.withTracerFactory(::LoggingTracer) // Only for debugging, performance overhead
    val pieComponent = DaggerRootPieComponent.builder()
        .rootPieModule(pieModule)
        .loggerComponent(loggerComponent)
        .resourceServiceComponent(resourceServiceComponent)
        .build()

    pieModule.addTaskDefs(
        languageComponent.runBenchmarkTask,
        languageComponent.prepareBenchmarkTask,
    )

    val benchComponent = mb.ccbench.tiger.di.DaggerTigerBenchComponent.builder()
        .benchLoggerComponent(loggerComponent)
        .tigerResourcesComponent(resourcesComponent)
        .benchResourceServiceComponent(resourceServiceComponent)
        .benchPlatformComponent(platformComponent)
        .tigerBenchLanguageComponent(languageComponent)
        .rootPieComponent(pieComponent)
        .build()

    benchComponent.mainCommand.main(args)
}

