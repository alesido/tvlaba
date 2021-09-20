package org.alsi.android.tvlaba.tv.vod

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VodGuideStartFragmentProvider {

    @ContributesAndroidInjector(modules = [VodGuideStartFragmentModule::class])
    abstract fun provideVodGuideStartFragmentFactory(): VodGuideStartFragment
}