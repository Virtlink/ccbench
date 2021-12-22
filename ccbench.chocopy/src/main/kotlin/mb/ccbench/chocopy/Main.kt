package mb.ccbench.chocopy

import mb.ccbench.di.BenchLoggerModule
import mb.ccbench.di.DaggerBenchLoggerComponent
import mb.ccbench.di.DaggerBenchResourceServiceComponent
import mb.ccbench.chocopy.di.DaggerChocopyBenchComponent
import mb.ccbench.chocopy.di.ChocopyBenchModule
import mb.pie.dagger.DaggerRootPieComponent
import mb.pie.dagger.RootPieModule
import mb.pie.runtime.PieBuilderImpl
import mb.resource.dagger.RootResourceServiceModule
import mb.resource.text.TextResourceRegistry
import mb.spoofax.core.platform.DaggerPlatformComponent
import mb.chocopy.DaggerChocopyComponent
import mb.chocopy.DaggerChocopyResourcesComponent
import mb.chocopy.ChocopyModule
import mb.rv32im.DaggerRv32ImComponent
import mb.rv32im.DaggerRv32ImResourcesComponent

fun main(args: Array<String>) {
    val loggerComponent = DaggerBenchLoggerComponent.builder()
        .benchLoggerModule(BenchLoggerModule())
        .build()

    val resourcesComponent = DaggerChocopyResourcesComponent.create()

    val textResourceRegistry = TextResourceRegistry()

    val resourceServiceModule = RootResourceServiceModule()
        .addRegistriesFrom(resourcesComponent)
        .addRegistry(textResourceRegistry)

    val resourceServiceComponent = DaggerBenchResourceServiceComponent.builder()
        .rootResourceServiceModule(resourceServiceModule)
        .benchLoggerComponent(loggerComponent)
        .build()

    val platformComponent = DaggerPlatformComponent.builder()
        .loggerComponent(loggerComponent)
        .resourceServiceComponent(resourceServiceComponent)
        .build()

    val rv32ImResourcesComponent = DaggerRv32ImResourcesComponent.builder()
        .build()

    val rv32ImComponent = DaggerRv32ImComponent.builder()
        .loggerComponent(loggerComponent)
        .rv32ImResourcesComponent(rv32ImResourcesComponent)
        .resourceServiceComponent(resourceServiceComponent)
        .platformComponent(platformComponent)
        .build()

    val languageComponent = DaggerChocopyComponent.builder()
        .chocopyModule(ChocopyModule())
        .loggerComponent(loggerComponent)
        .chocopyResourcesComponent(resourcesComponent)
        .resourceServiceComponent(resourceServiceComponent)
        .platformComponent(platformComponent)
        .rv32ImComponent(rv32ImComponent)
        .build()

    // PIE
    val rootPieModule = RootPieModule({ PieBuilderImpl() }, languageComponent)
        //.withTracerFactory(::LoggingTracer) // Only for debugging, performance overhead

    val rootPieComponent = DaggerRootPieComponent.builder()
        .rootPieModule(rootPieModule)
        .loggerComponent(loggerComponent)
        .resourceServiceComponent(resourceServiceComponent)
        .build()

    val chocopyBenchModule = ChocopyBenchModule(
        loggerComponent.loggerFactory,
        textResourceRegistry,
        languageComponent.strategoRuntimeProvider
    )

    val benchComponent = DaggerChocopyBenchComponent.builder()
        .chocopyBenchModule(chocopyBenchModule)
        .chocopyComponent(languageComponent)
        .rootPieComponent(rootPieComponent)
        .build()

    rootPieModule.addTaskDefs(
        benchComponent.buildBenchmarkTask,
        benchComponent.runBenchmarkTask,
    )

    benchComponent.mainCommand.main(args)
}

