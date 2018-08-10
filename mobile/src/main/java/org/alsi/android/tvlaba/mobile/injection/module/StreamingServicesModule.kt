package org.alsi.android.tvlaba.mobile.injection.module

import dagger.Module
import dagger.Provides
import org.alsi.android.domain.streaming.model.StreamingService
import org.alsi.android.domain.streaming.model.StreamingServiceRegistry
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.MoidomServiceBuilder
import javax.inject.Named
import javax.inject.Singleton

@Module
class StreamingServicesModule {

    @Singleton @Provides @Named("${Moidom.TAG}.${StreamingService.TV}")
    fun provideServiceIdMoidomTv(): Long = MOIDOM_TV_ID

    @Singleton @Provides @Named("${Moidom.TAG}.${StreamingService.VOD}")
    fun provideServiceIdMoidomVod(): Long = MOIDOM_VOD_ID

    @Singleton
    @Provides
    fun provideServiceRegistry(serviceBuilder: MoidomServiceBuilder) = listOf(
            serviceBuilder.buildTv(MOIDOM_ID, MOIDOM_TV_ID,
                    "${Moidom.TAG}.${StreamingService.TV}"),
            serviceBuilder.buildVod(MOIDOM_ID, MOIDOM_VOD_ID,
                    "${Moidom.TAG}.${StreamingService.VOD}")
        ) as StreamingServiceRegistry

    companion object {
        const val MOIDOM_ID = 1L
        const val MEGOGO_ID = 2L

        const val MOIDOM_TV_ID = 1L
        const val MOIDOM_VOD_ID = 2L
        const val MEGOGO_VOD_ID = 3L
    }
}