package org.alsi.android.tvlaba.tv.injection.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.alsi.android.tvlaba.tv.tv.TvChannelDirectoryFragment
import org.alsi.android.tvlaba.tv.tv.TvPlaybackAndScheduleFragment
import org.alsi.android.tvlaba.tv.tv.directory.TvChannelDirectoryFragmentModule
import org.alsi.android.tvlaba.tv.tv.directory.TvPlaybackAndScheduleFragmentModule

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [TvChannelDirectoryFragmentModule::class])
    abstract fun bindTvChannelDirectoryFragment(): TvChannelDirectoryFragment

    @ContributesAndroidInjector(modules = [TvPlaybackAndScheduleFragmentModule::class])
    abstract fun bindTvPlaybackAndScheduleFragment(): TvPlaybackAndScheduleFragment
}