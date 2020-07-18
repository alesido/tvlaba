package org.alsi.android.tvlaba.tv.injection.module

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

@Module
internal abstract class ApplicationModule {

    @Binds
    abstract fun bindContext(application: Application): Context
}