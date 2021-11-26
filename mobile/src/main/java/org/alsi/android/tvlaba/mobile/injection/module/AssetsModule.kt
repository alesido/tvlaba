package org.alsi.android.tvlaba.mobile.injection.module

import android.content.Context
import dagger.Module
import dagger.Provides
import org.alsi.android.domain.exception.model.ExceptionMessages
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.domain.vod.model.guide.directory.VodUnitTitles
import org.alsi.android.moidom.Moidom
import org.alsi.android.tvlaba.mobile.exception.ExceptionMessageStrings
import org.alsi.android.tvlaba.mobile.tv.TvServiceTelecolaPresentation
import org.alsi.android.tvlaba.mobile.vod.VodServicePrimeHdPresentation
import org.alsi.android.tvlaba.mobile.vod.VodUnitTitleStrings
import javax.inject.Named
import javax.inject.Singleton

@Module
class AssetsModule {

    @Singleton @Provides
    fun provideExceptionMessages(context: Context): ExceptionMessages
    = ExceptionMessageStrings(context)

    @Singleton @Provides
    fun provideVodUnitTitles(context: Context): VodUnitTitles
            = VodUnitTitleStrings(context)

    @Singleton @Provides @Named("${Moidom.TAG}.${StreamingService.TV}")
    fun provideTvServiceMoiDomPresentation(
        context: Context,
        @Named("${Moidom.TAG}.${StreamingService.TV}") serviceId: Long
    ): StreamingServicePresentation = TvServiceTelecolaPresentation(context, serviceId)

    @Singleton @Provides @Named("${Moidom.TAG}.${StreamingService.VOD}")
    fun provideVodServiceMoiDomPresentation(
        context: Context,
        @Named("${Moidom.TAG}.${StreamingService.VOD}") serviceId: Long
    ): StreamingServicePresentation = VodServicePrimeHdPresentation(context, serviceId)

}