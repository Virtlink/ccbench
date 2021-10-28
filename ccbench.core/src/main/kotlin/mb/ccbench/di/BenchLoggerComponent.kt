package mb.ccbench.di

import dagger.Component
import dagger.Module
import dagger.Provides
import mb.log.api.LoggerFactory
import mb.log.dagger.LoggerComponent
import mb.log.dagger.LoggerScope
import mb.log.slf4j.SLF4JLoggerFactory

@LoggerScope
@Component(modules = [
  BenchLoggerModule::class
])
interface BenchLoggerComponent: LoggerComponent

@Module
class BenchLoggerModule {
    @Provides
    @LoggerScope fun provideLoggerFactory(): LoggerFactory = SLF4JLoggerFactory()
}
