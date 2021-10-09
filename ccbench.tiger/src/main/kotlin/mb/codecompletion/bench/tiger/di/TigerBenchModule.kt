package mb.codecompletion.bench.tiger.di

import dagger.Module
import dagger.Provides
import mb.resource.text.TextResourceRegistry
import org.spoofax.terms.io.SimpleTextTermWriter

@Module
class TigerBenchModule(
    private val textResourceRegistry: TextResourceRegistry
) {

    @Provides fun provideTextResourceRegistry(): TextResourceRegistry = this.textResourceRegistry

    @Provides fun provideSimpleTextTermWriter(): SimpleTextTermWriter =
        SimpleTextTermWriter()

}
