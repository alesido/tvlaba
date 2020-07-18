package org.alsi.android.tvlaba.tv.injection.module

import dagger.Module
import dagger.Provides
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.streaming.model.service.StreamingServiceRegistry
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.repository.tv.TvServiceMoidom
import org.alsi.android.moidom.repository.vod.VodServiceMoidom
import org.alsi.android.tvlaba.tv.model.StreamingServiceDefaultsTv
import javax.inject.Named
import javax.inject.Singleton

@Module
class StreamingServicesModule {

    @Singleton @Provides @Named(Moidom.TAG)
    fun provideMoidomServiceName(): String = "Moi Dom Service"


    @Singleton @Provides @Named(Moidom.TAG)
    fun provideProviderIdMoidom(): Long = MOIDOM_ID

    @Singleton @Provides @Named("${Moidom.TAG}.${StreamingService.TV}")
    fun provideServiceIdMoidomTv(): Long = MOIDOM_TV_ID


    @Singleton @Provides @Named("${Moidom.TAG}.${StreamingService.VOD}")
    fun provideServiceIdMoidomVod(): Long = MOIDOM_VOD_ID

    @Singleton @Provides
    fun provideStreamingServiceDefaults(): StreamingServiceDefaults = StreamingServiceDefaultsTv()

    @Singleton
    @Provides
    fun provideServiceRegistry(tvServiceMoidom: TvServiceMoidom, vodServiceMoidom: VodServiceMoidom)
            = StreamingServiceRegistry(listOf(tvServiceMoidom, vodServiceMoidom))

    companion object {
        const val MOIDOM_ID = 1L
        const val MEGOGO_ID = 2L

        const val MOIDOM_TV_ID = 1L
        const val MOIDOM_VOD_ID = 2L
        const val MEGOGO_VOD_ID = 3L
    }
}