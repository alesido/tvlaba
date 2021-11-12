package org.alsi.android.tvlaba.settings.parental

import dagger.Module
import dagger.Provides

@Module
class ParentalControlSettingFragmentModule {

    @Provides
    fun provideParentalControlSettingFragmentView(fragment: ParentalControlSettingFragment) = fragment
}