package org.alsi.android.tvlaba.settings.parental

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ParentalControlPinFragmentProvider {

    @ContributesAndroidInjector(modules = [ParentalControlPinFragmentModule::class])
    abstract fun provideParentalControlPinFragmentFactory(): ParentalControlPinFragment
}