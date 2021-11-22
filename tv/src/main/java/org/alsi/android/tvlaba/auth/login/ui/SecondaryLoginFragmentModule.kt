package org.alsi.android.tvlaba.auth.login.ui

import dagger.Module
import dagger.Provides
import org.alsi.android.tvlaba.tv.tv.TvGuideStartFragment

@Module
class SecondaryLoginFragmentModule {

    @Provides
    fun provideLoginFragmentView(secondaryLoginFragment: SecondaryLoginFragment) = secondaryLoginFragment
}