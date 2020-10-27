package org.alsi.android.tvlaba.tv.tv.playback

import dagger.Module
import dagger.Provides

@Module
class TvPlaybackPreferencesFragmentModule {

    @Provides
    fun provideChannelDirectoryFragmentView(fragment: TvPlaybackPreferencesFragment) = fragment
}