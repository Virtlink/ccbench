package mb.ccbench.di

import dagger.Component
import dagger.Module
import dagger.Provides
import mb.resource.dagger.ResourceServiceScope
import mb.resource.dagger.RootResourceServiceComponent
import mb.resource.dagger.RootResourceServiceModule
import mb.resource.text.TextResourceRegistry

@ResourceServiceScope
@Component(
    modules = [
        RootResourceServiceModule::class,
    ],
    dependencies = [
        BenchLoggerComponent::class
    ]
)
interface BenchResourceServiceComponent : RootResourceServiceComponent


@Module
class BenchResourceServiceModule(
    private val textResourceRegistry: TextResourceRegistry
) {
    @Provides
    fun provideTextResourceRegistry(): TextResourceRegistry {
        return textResourceRegistry
    }
}
