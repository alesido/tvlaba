package org.alsi.android.local

import android.content.Context
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import org.alsi.android.local.model.MyObjectBox
import javax.inject.Named
import javax.inject.Singleton

@Module
class LocalModule {

    @Singleton
    @Provides
    @Named(Local.STORE_NAME)
    fun provideCommonLocalStore(context: Context): BoxStore {
        return MyObjectBox.builder().name(Local.STORE_NAME).androidContext(context)
            .maxReaders(252).build()
    }
}