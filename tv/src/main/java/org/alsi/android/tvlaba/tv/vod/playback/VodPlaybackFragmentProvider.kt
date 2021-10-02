package org.alsi.android.tvlaba.tv.vod.playback

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VodPlaybackFragmentProvider {

    @ContributesAndroidInjector(modules = [VodPlaybackFragmentModule::class])
    abstract fun provideVodPlaybackFragmentFactory(): VodPlaybackFragment
}