package org.alsi.android.tvlaba.tv.tv.playback

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TvPlaybackAndScheduleFragmentProvider {

    @ContributesAndroidInjector(modules = [TvPlaybackAndScheduleFragmentModule::class])
    abstract fun provideTvPlaybackAndScheduleFragmentFactory(): TvPlaybackAndScheduleFragment
}