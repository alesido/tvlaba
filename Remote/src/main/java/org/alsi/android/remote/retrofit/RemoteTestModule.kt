package org.alsi.android.remote.retrofit

import dagger.Module
import dagger.Provides
import org.alsi.android.remote.retrofit.error.RetrofitExceptionProducer
import javax.inject.Singleton

@Module
class RemoteTestModule {
    @Singleton @Provides fun provideRetrofitExceptionProducer(): RetrofitExceptionProducer
    = RetrofitExceptionProducer(isActivated = true)
}