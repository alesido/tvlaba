package org.alsi.android.tvlaba.tv.injection.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.alsi.android.tvlaba.tv.tv.directory.TvChannelDirectoryFragment
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackAndScheduleFragment
import org.alsi.android.tvlaba.tv.tv.directory.TvChannelDirectoryFragmentModule
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackAndScheduleFragmentModule
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackPreferencesFragment
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackPreferencesFragmentModule
import org.alsi.android.tvlaba.tv.tv.program.TvProgramDetailsFragment
import org.alsi.android.tvlaba.tv.tv.program.TvProgramDetailsFragmentModule

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [TvChannelDirectoryFragmentModule::class])
    abstract fun bindTvChannelDirectoryFragment(): TvChannelDirectoryFragment

    @ContributesAndroidInjector(modules = [TvPlaybackAndScheduleFragmentModule::class])
    abstract fun bindTvPlaybackAndScheduleFragment(): TvPlaybackAndScheduleFragment

    @ContributesAndroidInjector(modules = [TvPlaybackPreferencesFragmentModule::class])
    abstract fun bindTvPlaybackPreferencesFragment(): TvPlaybackPreferencesFragment

    @ContributesAndroidInjector(modules = [TvProgramDetailsFragmentModule::class])
    abstract fun bindTvProgramDetailsFragment(): TvProgramDetailsFragment
}