package org.alsi.android.moidom

import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datatv.repository.TvChannelDataRepository
import org.alsi.android.domain.streaming.model.*
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.local.model.user.UserAccountEntity
import org.alsi.android.local.store.tv.TvChannelLocalStoreDelegate
import org.alsi.android.moidom.repository.*
import org.alsi.android.moidom.repository.tv.TvProgramRepositoryMoidom
import org.alsi.android.moidom.repository.tv.TvVideoStreamRepositoryMoidom
import org.alsi.android.moidom.store.DataServiceFactoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.moidom.store.tv.TvChannelRemoteStoreMoidom
import javax.inject.Named
import javax.inject.Singleton

@Module
class MoidomModule {

    @Singleton @Provides
    fun provideServiceContext(): ContextMoidom = ContextMoidom()

    @Singleton @Provides fun provideRestServiceMoiDom(): RestServiceMoidom {
        return DataServiceFactoryMoidom.makeRestServiceMoidom()
    }

    @Singleton @Provides fun provideUserAccountSubject(): PublishSubject<UserAccountEntity> = PublishSubject.create()

    @Singleton @Provides @Named(Moidom.TV_DIRECTORY_REPOSITORY)
    fun provideTvDirectoryRepository(

            context: Context,
            @Named("${Moidom.TAG}.${StreamingService.TV}") moidomTvServiceId: Long,
            userAccountSubject: PublishSubject<UserAccountEntity>)

            : DirectoryRepository {

        //val tvChannelLocalStore = TvChannelLocalStoreDelegate(moidomTvServiceId)

        return TvDirectoryRepository(
                moidomTvServiceId,
                TvChannelDataRepository(
                        TvChannelRemoteStoreMoidom(),
                        TvChannelLocalStoreDelegate(moidomTvServiceId)),
                TvProgramRepositoryMoidom(),
                TvVideoStreamRepositoryMoidom())
    }

    @Singleton @Provides @Named(Moidom.VOD_DIRECTORY_REPOSITORY)
    fun provideVodDirectoryRepository(): DirectoryRepository = VodDirectoryRepositoryMoidom()

    @Singleton @Provides fun provideSettingsRepository(): SettingsRepository = SettingsRepositoryMoidom()
    @Singleton @Provides fun provideDeviceDataRepository(): DeviceDataRepository = DeviceDataRepositoryMoidom()
    @Singleton @Provides fun provideSessionRepository(): SessionRepository = SessionRepositoryMoidom()
}