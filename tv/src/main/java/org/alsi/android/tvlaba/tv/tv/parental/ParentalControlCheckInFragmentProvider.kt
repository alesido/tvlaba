package org.alsi.android.tvlaba.tv.tv.parental

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ParentalControlCheckInFragmentProvider {

    @ContributesAndroidInjector(modules = [ParentalControlCheckInFragmentModule::class])
    abstract fun provideParentalControlCheckInFragmentFactory(): ParentalControlCheckInFragment
}