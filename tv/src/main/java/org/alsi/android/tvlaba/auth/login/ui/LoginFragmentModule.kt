package org.alsi.android.tvlaba.auth.login.ui

import dagger.Module
import dagger.Provides
import org.alsi.android.tvlaba.tv.tv.TvGuideStartFragment

@Module
class LoginFragmentModule {

    @Provides
    fun provideLoginFragmentView(loginFragment: LoginFragment) = loginFragment
}