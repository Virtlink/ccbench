package mb.ccbench.chocopy.di

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import dagger.Component
import dagger.Module
import dagger.Provides
import mb.ccbench.di.BenchScope
import mb.ccbench.chocopy.ChocopyBuildBenchmarkTask
import mb.ccbench.chocopy.ChocopyRunBenchmarkTask
import mb.ccbench.chocopy.cli.ChocopyMainCommand
import mb.log.api.LoggerFactory
import mb.pie.dagger.RootPieComponent
import mb.resource.text.TextResourceRegistry
import mb.stratego.common.StrategoRuntime
import mb.tego.strategies.runtime.TegoRuntime
import mb.tego.strategies.runtime.TegoRuntimeImpl
import mb.chocopy.ChocopyComponent
import mb.chocopy.ChocopyQualifier
import org.spoofax.terms.io.SimpleTextTermWriter
import javax.inject.Provider

@BenchScope
@Component(
  modules = [
    ChocopyBenchModule::class,
  ],
  dependencies = [
    ChocopyComponent::class,
    RootPieComponent::class,
  ]
)
interface ChocopyBenchComponent {
    val mainCommand: ChocopyMainCommand
    val runBenchmarkTask: ChocopyRunBenchmarkTask
    val buildBenchmarkTask: ChocopyBuildBenchmarkTask
}

@Module
class ChocopyBenchModule(
    private val loggerFactory: LoggerFactory,
    private val textResourceRegistry: TextResourceRegistry,
    @ChocopyQualifier private val strategoRuntimeProvider: Provider<StrategoRuntime>
) {

    @Provides fun provideTextResourceRegistry(): TextResourceRegistry =
        this.textResourceRegistry

    @Provides @BenchScope fun provideSimpleTextTermWriter(): SimpleTextTermWriter =
        SimpleTextTermWriter()

    @Provides fun provideStrategoRuntime(): StrategoRuntime =
        strategoRuntimeProvider.get()

    @Provides @BenchScope fun provideTegoRuntime(): TegoRuntime =
        TegoRuntimeImpl(loggerFactory)

    @Provides @BenchScope fun provideTerminal(): Terminal =
        Terminal(AnsiLevel.TRUECOLOR)
}