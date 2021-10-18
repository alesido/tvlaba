package org.alsi.android.tvlaba.settings.parental

import dagger.Module
import dagger.Provides

@Module
class ParentalControlPinFragmentModule {

    @Provides
    fun provideParentalControlPinFragmentView(fragment: ParentalControlPinFragment) = fragment
}