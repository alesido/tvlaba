package org.alsi.android.presentationtv

import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import org.alsi.android.presentationtv.model.TvChannelPlaybackEvent
import javax.inject.Singleton

@Module
class PresentationTvModule {

    @Singleton
    @Provides
    fun provideChannelPlaybackEventSubject(): PublishSubject<TvChannelPlaybackEvent>
            = PublishSubject.create()
}