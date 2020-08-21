package org.alsi.android.tvlaba.tv.tv.playback

import dagger.Module
import dagger.Provides

@Module
class TvPlaybackAndScheduleFragmentModule {

    @Provides
    fun provideChannelDirectoryFragmentView(fragment: TvPlaybackAndScheduleFragment) = fragment
}