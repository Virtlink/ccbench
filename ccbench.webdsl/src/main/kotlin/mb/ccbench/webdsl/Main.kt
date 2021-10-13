package mb.ccbench.webdsl

import mb.ccbench.di.BenchLoggerModule
import mb.ccbench.di.DaggerBenchLoggerComponent
import mb.ccbench.di.DaggerBenchPlatformComponent
import mb.ccbench.di.DaggerBenchResourceServiceComponent
import mb.ccbench.webdsl.di.DaggerWebDSLBenchComponent
import mb.ccbench.webdsl.di.DaggerWebDSLBenchLanguageComponent
import mb.ccbench.webdsl.di.WebDSLBenchModule
import mb.pie.dagger.DaggerRootPieComponent
import mb.pie.dagger.RootPieModule
import mb.pie.runtime.PieBuilderImpl
import mb.resource.dagger.RootResourceServiceModule
import mb.resource.text.TextResourceRegistry
import mb.tego.strategies.DaggerTegoComponent
import mb.webdsl.DaggerWebDSLResourcesComponent
import mb.webdsl.WebDSLModule

fun main(args: Array<String>) {

    // Platform:
    val loggerComponent = DaggerBenchLoggerComponent.builder()
        .benchLoggerModule(BenchLoggerModule())
        .build()

    // Language:
    val resourcesComponent = DaggerWebDSLResourcesComponent.create()
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

    val tegoComponent = DaggerTegoComponent.builder()
        .loggerComponent(loggerComponent)
        .build()

    // Language:
    val languageComponent = DaggerWebDSLBenchLanguageComponent.builder()
        .webDSLModule(WebDSLModule())
        .webDSLBenchModule(WebDSLBenchModule(textResourceRegistry))
        .benchLoggerComponent(loggerComponent)
        .resourceServiceComponent(resourceServiceComponent)
        .benchPlatformComponent(platformComponent)
        .webDSLResourcesComponent(resourcesComponent)
        .tegoComponent(tegoComponent)
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
        languageComponent.buildBenchmarkTask,
        languageComponent.runBenchmarkTask,
    )

    val benchComponent = DaggerWebDSLBenchComponent.builder()
        .benchLoggerComponent(loggerComponent)
        .webDSLResourcesComponent(resourcesComponent)
        .benchResourceServiceComponent(resourceServiceComponent)
        .benchPlatformComponent(platformComponent)
        .webDSLBenchLanguageComponent(languageComponent)
        .rootPieComponent(pieComponent)
        .tegoComponent(tegoComponent)
        .build()

    benchComponent.mainCommand.main(args)
}

