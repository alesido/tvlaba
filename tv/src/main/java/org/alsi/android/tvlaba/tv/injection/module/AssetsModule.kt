package org.alsi.android.tvlaba.tv.injection.module

import android.content.Context
import dagger.Module
import dagger.Provides
import org.alsi.android.domain.exception.model.ExceptionMessages
import org.alsi.android.tvlaba.exception.ExceptionMessageStrings
import javax.inject.Singleton

@Module
class AssetsModule {

    @Singleton @Provides
    fun provideExceptionMessages(context: Context): ExceptionMessages
    = ExceptionMessageStrings(context)
}