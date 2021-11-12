package org.alsi.android.tvlaba.settings.parental

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ParentalControlSettingFragmentProvider {

    @ContributesAndroidInjector(modules = [ParentalControlSettingFragmentModule::class])
    abstract fun provideParentalControlSettingFragmentFactory(): ParentalControlSettingFragment
}