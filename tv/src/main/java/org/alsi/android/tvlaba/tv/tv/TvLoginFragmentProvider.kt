package org.alsi.android.tvlaba.tv.tv

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.alsi.android.tvlaba.auth.login.ui.LoginFragment
import org.alsi.android.tvlaba.auth.login.ui.LoginFragmentModule

@Module
abstract class TvLoginFragmentProvider {

    @ContributesAndroidInjector(modules = [LoginFragmentModule::class])
    abstract fun provideLoginFragmentFactory(): LoginFragment
}