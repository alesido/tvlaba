package org.alsi.android.tvlaba.tv

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppStartFragmentProvider {

    @ContributesAndroidInjector(modules = [AppStartFragmentModule::class])
    abstract fun provideAppStartFragmentFactory(): AppStartFragment
}