package org.alsi.android.tvlaba.tv.tv.directory

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.alsi.android.tvlaba.tv.tv.TvChannelDirectoryFragment
import org.alsi.android.tvlaba.tv.tv.TvPlaybackAndScheduleFragment

@Module
abstract class TvPlaybackAndScheduleFragmentProvider {

    @ContributesAndroidInjector(modules = [TvPlaybackAndScheduleFragmentModule::class])
    abstract fun provideTvPlaybackAndScheduleFragmentFactory(): TvPlaybackAndScheduleFragment
}