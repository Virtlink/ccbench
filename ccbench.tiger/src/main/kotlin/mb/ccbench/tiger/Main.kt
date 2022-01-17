package mb.ccbench.tiger

import mb.ccbench.di.BenchLoggerModule
import mb.ccbench.di.DaggerBenchLoggerComponent
import mb.ccbench.di.DaggerBenchResourceServiceComponent
import mb.ccbench.tiger.di.DaggerTigerBenchComponent
import mb.ccbench.tiger.di.TigerBenchComponent
import mb.ccbench.tiger.di.TigerBenchModule
import mb.pie.dagger.DaggerRootPieComponent
import mb.pie.dagger.RootPieModule
import mb.pie.runtime.PieBuilderImpl
import mb.resource.dagger.RootResourceServiceModule
import mb.resource.text.TextResourceRegistry
import mb.spoofax.core.platform.DaggerPlatformComponent
import mb.tiger.DaggerTigerComponent
import mb.tiger.DaggerTigerResourcesComponent
import mb.tiger.TigerModule

fun main(args: Array<String>) {
    createBenchComponent().mainCommand.main(args)
}

fun createBenchComponent(): TigerBenchComponent {
    val loggerComponent = DaggerBenchLoggerComponent.builder()
        .benchLoggerModule(BenchLoggerModule())
        .build()

    val resourcesComponent = DaggerTigerResourcesComponent.create()

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

    val languageComponent = DaggerTigerComponent.builder()
        .tigerModule(TigerModule())
        .loggerComponent(loggerComponent)
        .tigerResourcesComponent(resourcesComponent)
        .resourceServiceComponent(resourceServiceComponent)
        .platformComponent(platformComponent)
        .build()

    // PIE
    val rootPieModule = RootPieModule({ PieBuilderImpl() }, languageComponent)
    //.withTracerFactory(::LoggingTracer) // Only for debugging, performance overhead

    val rootPieComponent = DaggerRootPieComponent.builder()
        .rootPieModule(rootPieModule)
        .loggerComponent(loggerComponent)
        .resourceServiceComponent(resourceServiceComponent)
        .build()

    val tigerBenchModule = TigerBenchModule(
        loggerComponent.loggerFactory,
        textResourceRegistry,
        languageComponent.strategoRuntimeProvider
    )

    val benchComponent = DaggerTigerBenchComponent.builder()
        .tigerBenchModule(tigerBenchModule)
        .tigerComponent(languageComponent)
        .rootPieComponent(rootPieComponent)
        .build()

    rootPieModule.addTaskDefs(
        benchComponent.buildBenchmarkTask,
        benchComponent.runBenchmarkTask,
    )

    return benchComponent
}
