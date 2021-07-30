package org.alsi.android.tvlaba.mobile.injection.module

import android.content.Context
import dagger.Module
import dagger.Provides
import org.alsi.android.domain.exception.model.ExceptionMessages
import org.alsi.android.tvlaba.mobile.exception.ExceptionMessageStrings
import javax.inject.Singleton

@Module
class AssetsModule {

    @Singleton @Provides
    fun provideExceptionMessages(context: Context): ExceptionMessages
    = ExceptionMessageStrings(context)
}