package org.alsi.android.tvlaba.tv.tv.playback

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TvPlaybackPreferencesFragmentProvider {

    @ContributesAndroidInjector(modules = [TvPlaybackPreferencesFragmentModule::class])
    abstract fun provideTvPlaybackPreferencesFactory(): TvPlaybackPreferencesFragment
}