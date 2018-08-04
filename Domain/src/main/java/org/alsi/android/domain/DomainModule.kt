package org.alsi.android.domain

import dagger.Module
import dagger.Provides
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.streaming.model.StreamingServiceRegistry
import javax.inject.Singleton

@Module
class DomainModule {

    @Singleton
    @Provides
    fun providePresentationManager(streamingServiceRegistry: StreamingServiceRegistry): PresentationManager {
        return PresentationManager(streamingServiceRegistry)
    }
}