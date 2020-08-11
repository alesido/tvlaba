package org.alsi.android.tvlaba.tv.tv.directory

import dagger.Module
import dagger.Provides
import org.alsi.android.tvlaba.tv.tv.TvPlaybackAndScheduleFragment

@Module
class TvPlaybackAndScheduleFragmentModule {

    @Provides
    fun provideChannelDirectoryFragmentView(fragment: TvPlaybackAndScheduleFragment) = fragment
}