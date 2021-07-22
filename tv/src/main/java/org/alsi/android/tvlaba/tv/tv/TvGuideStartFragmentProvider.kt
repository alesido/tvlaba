package org.alsi.android.tvlaba.tv.tv

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class TvGuideStartFragmentProvider {

    @ContributesAndroidInjector(modules = [TvGuideStartFragmentModule::class])
    abstract fun provideTvGuideStartFragmentFactory(): TvGuideStartFragment
}