package org.alsi.android.tvlaba.settings

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class GeneralSettingsFragmentProvider {

    @ContributesAndroidInjector(modules = [GeneralSettingsFragmentModule::class])
    abstract fun provideSettingsFragmentFactory(): GeneralSettingsFragment
}