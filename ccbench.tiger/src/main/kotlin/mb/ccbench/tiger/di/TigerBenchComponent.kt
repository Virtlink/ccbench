package mb.ccbench.tiger.di

import dagger.Component
import dagger.Module
import dagger.Provides
import mb.ccbench.di.BenchScope
import mb.ccbench.tiger.TigerBuildBenchmarkTask
import mb.ccbench.tiger.TigerRunBenchmarkTask
import mb.ccbench.tiger.cli.TigerMainCommand
import mb.log.api.LoggerFactory
import mb.pie.dagger.PieComponent
import mb.pie.dagger.RootPieComponent
import mb.resource.text.TextResourceRegistry
import mb.stratego.common.StrategoRuntime
import mb.tego.strategies.runtime.TegoRuntime
import mb.tego.strategies.runtime.TegoRuntimeImpl
import mb.tiger.TigerComponent
import mb.tiger.TigerQualifier
import org.spoofax.terms.io.SimpleTextTermWriter
import javax.inject.Provider

@BenchScope
@Component(
  modules = [
    TigerBenchModule::class,
  ],
  dependencies = [
    TigerComponent::class,
    RootPieComponent::class,
  ]
)
interface TigerBenchComponent {
    val mainCommand: TigerMainCommand
    val runBenchmarkTask: TigerRunBenchmarkTask
    val buildBenchmarkTask: TigerBuildBenchmarkTask
}

@Module
class TigerBenchModule(
    private val loggerFactory: LoggerFactory,
    private val textResourceRegistry: TextResourceRegistry,
    @TigerQualifier private val strategoRuntimeProvider: Provider<StrategoRuntime>
) {

    @Provides fun provideTextResourceRegistry(): TextResourceRegistry =
        this.textResourceRegistry

    @Provides fun provideSimpleTextTermWriter(): SimpleTextTermWriter =
        SimpleTextTermWriter()

    @Provides fun provideStrategoRuntime(): StrategoRuntime =
        strategoRuntimeProvider.get()

    @Provides fun provideTegoRuntime(): TegoRuntime =
        TegoRuntimeImpl(loggerFactory)
}