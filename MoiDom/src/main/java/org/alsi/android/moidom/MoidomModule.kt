package org.alsi.android.moidom

import android.content.Context
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import org.alsi.android.domain.streaming.model.DeviceDataRepository
import org.alsi.android.domain.streaming.model.DirectoryRepository
import org.alsi.android.domain.streaming.model.SessionRepository
import org.alsi.android.domain.streaming.model.SettingsRepository
import org.alsi.android.moidom.repository.*
import org.alsi.android.moidom.store.remote.AccountServiceRemoteMoidom
import org.alsi.android.moidom.store.AccountStoreLocalMoidom
import org.alsi.android.moidom.store.DataServiceFactoryMoidom
import org.alsi.android.moidom.store.remote.RestServiceMoidom
import javax.inject.Named
import javax.inject.Singleton

@Module
class MoidomModule {

    @Singleton @Provides
    fun provideServiceContext(): ContextMoidom = ContextMoidom()

    @Singleton @Provides fun provideRestServiceMoiDom(): RestServiceMoidom {
        return DataServiceFactoryMoidom.makeRestServiceMoidom()
    }

    @Singleton @Provides fun provideAccountDataServiceMoidom(
            remote: AccountServiceRemoteMoidom,
            local: AccountStoreLocalMoidom)
            : AccountDataServiceMoidom {
        return AccountDataServiceMoidom(remote, local)
    }

    @Singleton @Provides @Named(Moidom.TV_DIRECTORY_REPOSITORY)
    fun provideTvDirectoryRepository(): DirectoryRepository = TvDirectoryRepositoryMoidom()

    @Singleton @Provides @Named(Moidom.VOD_DIRECTORY_REPOSITORY)
    fun provideVodDirectoryRepository(): DirectoryRepository = VodDirectoryRepositoryMoidom()

    @Singleton @Provides fun provideSettingsRepository(): SettingsRepository = SettingsRepositoryMoidom()
    @Singleton @Provides fun provideDeviceDataRepository(): DeviceDataRepository = DeviceDataRepositoryMoidom()
    @Singleton @Provides fun provideSessionRepository(): SessionRepository = SessionRepositoryMoidom()

    @Singleton @Provides @Named(Moidom.INTERNAL_STORE_NAME)
    fun provideInternalStoreMoidom(context: Context): BoxStore {
        return DataServiceFactoryMoidom.makeInternalStoreService(context)
    }
}