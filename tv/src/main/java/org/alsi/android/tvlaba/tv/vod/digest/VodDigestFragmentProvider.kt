package org.alsi.android.tvlaba.tv.vod.digest

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VodDigestFragmentProvider {

    @ContributesAndroidInjector(modules = [VodDigestFragmentModule::class])
    abstract fun provideVodDigestFragmentFactory(): VodDigestFragment
}