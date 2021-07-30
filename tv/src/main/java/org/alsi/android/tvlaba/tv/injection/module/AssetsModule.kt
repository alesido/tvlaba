package org.alsi.android.tvlaba.tv.injection.module

import android.content.Context
import dagger.Module
import dagger.Provides
import org.alsi.android.domain.exception.model.ExceptionMessages
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.streaming.model.service.StreamingServiceRegistry
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.repository.tv.TvServiceMoidom
import org.alsi.android.moidom.repository.vod.VodServiceMoidom
import org.alsi.android.tvlaba.exception.ExceptionMessageStrings
import org.alsi.android.tvlaba.tv.model.StreamingServiceDefaultsTv
import javax.inject.Named
import javax.inject.Singleton

@Module
class AssetsModule {

    @Singleton @Provides
    fun provideExceptionMessages(context: Context): ExceptionMessages
    = ExceptionMessageStrings(context)
}