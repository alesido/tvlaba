package org.alsi.android.moidom

import dagger.Module
import dagger.Provides
import org.alsi.android.domain.streaming.model.DeviceDataRepository
import org.alsi.android.domain.streaming.model.DirectoryRepository
import org.alsi.android.domain.streaming.model.SessionRepository
import org.alsi.android.domain.streaming.model.SettingsRepository
import org.alsi.android.moidom.repository.*
import org.alsi.android.moidom.store.DataServiceFactoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import javax.inject.Named
import javax.inject.Singleton

@Module
class MoidomModule {

    @Singleton @Provides
    fun provideServiceContext(): ContextMoidom = ContextMoidom()

    @Singleton @Provides fun provideRestServiceMoiDom(): RestServiceMoidom {
        return DataServiceFactoryMoidom.makeRestServiceMoidom()
    }

    @Singleton @Provides @Named(Moidom.TV_DIRECTORY_REPOSITORY)
    fun provideTvDirectoryRepository(): DirectoryRepository = TvDirectoryRepositoryMoidom()

    @Singleton @Provides @Named(Moidom.VOD_DIRECTORY_REPOSITORY)
    fun provideVodDirectoryRepository(): DirectoryRepository = VodDirectoryRepositoryMoidom()

    @Singleton @Provides fun provideSettingsRepository(): SettingsRepository = SettingsRepositoryMoidom()
    @Singleton @Provides fun provideDeviceDataRepository(): DeviceDataRepository = DeviceDataRepositoryMoidom()
    @Singleton @Provides fun provideSessionRepository(): SessionRepository = SessionRepositoryMoidom()
}