package mb.ccbench.webdsl.di

import dagger.Component
import mb.ccbench.di.BenchLoggerComponent
import mb.ccbench.di.BenchPlatformComponent
import mb.ccbench.webdsl.WebDSLBuildBenchmarkTask
import mb.ccbench.webdsl.WebDSLRunBenchmarkTask
import mb.resource.dagger.ResourceServiceComponent
import mb.tego.strategies.TegoComponent
import mb.webdsl.WebDSLComponent
import mb.webdsl.WebDSLModule
import mb.webdsl.WebDSLResourcesComponent
import mb.webdsl.WebDSLScope
import org.spoofax.interpreter.terms.ITermFactory

@WebDSLScope
@Component(
    modules = [
        WebDSLModule::class,
        WebDSLBenchModule::class,
    ],
    dependencies = [
        BenchLoggerComponent::class,
        WebDSLResourcesComponent::class,
        ResourceServiceComponent::class,
        BenchPlatformComponent::class,
        TegoComponent::class,
    ]
)
interface WebDSLBenchLanguageComponent: WebDSLComponent {
    val termFactory: ITermFactory
    val buildBenchmarkTask: WebDSLBuildBenchmarkTask
    val runBenchmarkTask: WebDSLRunBenchmarkTask
}
