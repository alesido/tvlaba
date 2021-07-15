package org.alsi.android.tvlaba.tv

import dagger.Module
import dagger.Provides

@Module
class AppStartFragmentModule {

    @Provides
    fun provideAppStartFragmentView(appStartFragment: AppStartFragment) = appStartFragment
}