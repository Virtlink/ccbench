package mb.ccbench.tiger.di

import dagger.Component
import mb.ccbench.tiger.TigerBuildBenchmarkTask
import mb.ccbench.tiger.TigerRunBenchmarkTask
import mb.ccbench.di.BenchLoggerComponent
import mb.ccbench.di.BenchPlatformComponent
import mb.resource.dagger.ResourceServiceComponent
import mb.tiger.TigerComponent
import mb.tiger.TigerModule
import mb.tiger.TigerResourcesComponent
import mb.tiger.TigerScope
import org.spoofax.interpreter.terms.ITermFactory

@TigerScope
@Component(
  modules = [
    TigerModule::class,
    TigerBenchModule::class,
  ],
  dependencies = [
    BenchLoggerComponent::class,
    TigerResourcesComponent::class,
    ResourceServiceComponent::class,
    BenchPlatformComponent::class,
  ]
)
interface TigerBenchLanguageComponent: TigerComponent {
    val termFactory: ITermFactory
    val runBenchmarkTask: TigerRunBenchmarkTask
    val buildBenchmarkTask: TigerBuildBenchmarkTask
}
