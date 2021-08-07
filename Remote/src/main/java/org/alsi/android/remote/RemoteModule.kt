package org.alsi.android.remote

import dagger.Module
import dagger.Provides
import org.alsi.android.remote.retrofit.error.RetrofitExceptionProducer
import javax.inject.Singleton

@Module
class RemoteModule {
    @Singleton @Provides fun provideRetrofitExceptionProducer() : RetrofitExceptionProducer
    = RetrofitExceptionProducer(isActivated = false)
}