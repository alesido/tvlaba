package org.alsi.android.tvlaba.settings

import dagger.Module
import dagger.Provides

@Module
class GeneralSettingsFragmentModule {

    @Provides
    fun provideGeneralSettingsFragmentView(fragment: GeneralSettingsFragment) = fragment
}