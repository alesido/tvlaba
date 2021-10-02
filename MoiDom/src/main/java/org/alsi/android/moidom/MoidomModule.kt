package org.alsi.android.moidom

import android.content.Context
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datatv.repository.TvVideoStreamDataRepository
import org.alsi.android.datatv.store.*
import org.alsi.android.datavod.store.VodBrowseCursorLocalStore
import org.alsi.android.datavod.store.VodDirectoryLocalStore
import org.alsi.android.datavod.store.VodDirectoryRemoteStore
import org.alsi.android.datavod.store.VodPlayCursorLocalStore
import org.alsi.android.domain.streaming.model.ServiceProvider
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.tv.repository.guide.TvVideoStreamRepository
import org.alsi.android.local.Local
import org.alsi.android.local.model.user.UserAccountSubject
import org.alsi.android.local.store.AccountStoreLocalDelegate
import org.alsi.android.local.store.tv.*
import org.alsi.android.local.store.vod.VodBrowseCursorLocalStoreDelegate
import org.alsi.android.local.store.vod.VodDirectoryLocalStoreDelegate
import org.alsi.android.local.store.vod.VodPlayCursorLocalStoreDelegate
import org.alsi.android.moidom.Moidom.INTERNAL_STORE_NAME
import org.alsi.android.moidom.mapper.RetrofitExceptionMapper
import org.alsi.android.moidom.model.LoginEvent
import org.alsi.android.moidom.model.tv.MyObjectBox
import org.alsi.android.moidom.repository.AccountDataServiceMoidom
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.repository.ServiceProviderMoidom
import org.alsi.android.moidom.repository.SettingsRepositoryMoidom
import org.alsi.android.moidom.repository.tv.TvServiceMoidom
import org.alsi.android.moidom.repository.vod.VodServiceMoidom
import org.alsi.android.moidom.store.DataServiceFactoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.moidom.store.tv.TvChannelRemoteStoreMoidom
import org.alsi.android.moidom.store.tv.TvProgramRemoteStoreMoidom
import org.alsi.android.moidom.store.tv.TvVideoStreamRemoteStoreMoidom
import org.alsi.android.moidom.store.vod.VodDirectoryRemoteStoreMoiDom
import org.alsi.android.remote.retrofit.error.RetrofitExceptionProducer
import javax.inject.Named
import javax.inject.Singleton

@Module
class MoidomModule {

    /** MoiDom Services (list of)
     *
     * check for MutableList explanation @ https://stackoverflow.com/questions/45384389/why-cant-dagger-process-these-kotlin-generics
    */
    @Singleton @Provides @Named(Moidom.TAG) fun provideServicesMoidom(
            tvServiceMoiDom: TvServiceMoidom, vodServiceMoidom: VodServiceMoidom)
            : MutableList<StreamingService> = mutableListOf(tvServiceMoiDom, vodServiceMoidom)

    /**     MoiDom Service Provider
     */
    @Singleton @Provides fun provideServiceProviderMoidom(
            @Named(Moidom.TAG) id: Long,
            @Named(Moidom.TAG) name: String,
            accountService: AccountDataServiceMoidom,
            settingsRepository: SettingsRepositoryMoidom,
            @Named(Moidom.TAG) services: MutableList<StreamingService>)
            : ServiceProvider
            = ServiceProviderMoidom(id, name, accountService, settingsRepository, services)

    /**     MoiDom REST service
     */
    @Singleton @Provides fun provideRestServiceMoiDom(
        retrofitExceptionMapper: RetrofitExceptionMapper,
        instrumentedTestInterceptor: RetrofitExceptionProducer
    ) = DataServiceFactoryMoidom.makeRestServiceMoidom(
        retrofitExceptionMapper::map, instrumentedTestInterceptor)

    /**     Moidom service-wide Local Store for TV data
     */
    @Singleton @Provides @Named(INTERNAL_STORE_NAME)
    fun provideInternalStoreMoidom(context: Context): BoxStore
            = MyObjectBox.builder().name(INTERNAL_STORE_NAME).androidContext(context).build()

    /**     Moidom service-wide Local Store for TV data
     */
    @Named("${Moidom.TAG}.${StreamingService.TV}")
    @Singleton @Provides fun provideTvServiceLocalStoreMoidom(
            context: Context,
            @Named("${Moidom.TAG}.${StreamingService.TV}") serviceId: Long): BoxStore
            = org.alsi.android.local.model.MyObjectBox.builder().name("${Moidom.TAG}.${StreamingService.TV}.$serviceId")
            .androidContext(context).maxReaders(252).build()

    /**     Moidom service-wide Local Store for VOD data
     */
    @Named("${Moidom.TAG}.${StreamingService.VOD}")
    @Singleton @Provides fun provideVodServiceLocalStoreMoidom(
            context: Context,
            @Named("${Moidom.TAG}.${StreamingService.VOD}") serviceId: Long): BoxStore
            = org.alsi.android.local.model.MyObjectBox.builder().name("${Moidom.TAG}.${StreamingService.VOD}.$serviceId")
            .androidContext(context).maxReaders(252).build()

    @Singleton @Provides fun provideAccountStoreLocalDelegate(
        @Named(Local.STORE_NAME) boxStore: BoxStore,
        @Named("${Moidom.TAG}.${StreamingService.TV}") accountSubject: UserAccountSubject
    ): AccountStoreLocalDelegate = AccountStoreLocalDelegate(boxStore, accountSubject)

    /**     TV Channel Remote Store
     */
    @Singleton @Provides fun provideTvChannelRemoteStoreMoidom(
            remoteService: RestServiceMoidom, remoteSession: RemoteSessionRepositoryMoidom)
    : TvChannelRemoteStore = TvChannelRemoteStoreMoidom(remoteService, remoteSession)

    /**     TV Channel Local Store
     */
    @Singleton @Provides fun provideTvChannelLocalStoreMoidomDelegate(
            @Named("${Moidom.TAG}.${StreamingService.TV}") localBoxStore: BoxStore,
            @Named("${Moidom.TAG}.${StreamingService.TV}") accountSubject: UserAccountSubject)
    : TvChannelLocalStore = TvChannelLocalStoreDelegate(localBoxStore, accountSubject)

    /**     TV Program Remote Store
     */
    @Singleton @Provides fun provideTvProgramRemoteStoreMoidom(
            remoteService: RestServiceMoidom, remoteSession: RemoteSessionRepositoryMoidom)
    : TvProgramRemoteStore = TvProgramRemoteStoreMoidom(remoteService, remoteSession)

    /**     TV Program Local Store
     */
    @Singleton @Provides fun provideTvProgramLocalStoreMoidom(
        @Named("${Moidom.TAG}.${StreamingService.TV}") accountSubject: UserAccountSubject
    ) : TvProgramLocalStore = TvProgramLocalMemoryStoreDelegate(accountSubject)

    /**     TV Video Stream Local Store
     */
    @Singleton @Provides fun provideTvVideoStreamLocalStoreMoidomDelegate(
            @Named("${Moidom.TAG}.${StreamingService.TV}") localBoxStore: BoxStore)
    : TvVideoStreamLocalStore = TvVideoStreamLocalStoreDelegate(localBoxStore)

    /**     TV Video Stream Remote Store
     */
    @Singleton @Provides fun provideTvVideoStreamRemoteStoreMoiDom(
            remoteService: RestServiceMoidom, remoteSession: RemoteSessionRepositoryMoidom)
            : TvVideoStreamRemoteStore = TvVideoStreamRemoteStoreMoidom(remoteService, remoteSession)

    /**     TV Video Stream Repository
     */
    @Singleton @Provides fun provideTvVideoStreamRepositoryMoiDom(
            localStore: TvVideoStreamLocalStore, remoteStore: TvVideoStreamRemoteStore)
            : TvVideoStreamRepository = TvVideoStreamDataRepository(localStore, remoteStore)

    /**     TV Playback Cursor, Local Store
     */
    @Named("${Moidom.TAG}.${StreamingService.TV}")
    @Singleton @Provides fun provideTvPlayCursorLocalStoreMoidomDelegate(
            @Named("${Moidom.TAG}.${StreamingService.TV}") localBoxStore: BoxStore,
            @Named("${Moidom.TAG}.${StreamingService.TV}") accountChangeSubject: UserAccountSubject
    )
    : TvPlayCursorLocalStore = TvPlayCursorLocalStoreDelegate(localBoxStore, accountChangeSubject)

    /**     TV Browsing Cursor, Local Store
     */
    @Named("${Moidom.TAG}.${StreamingService.TV}")
    @Singleton @Provides fun provideTvBrowseCursorLocalStoreMoidomDelegate(
            @Named("${Moidom.TAG}.${StreamingService.TV}") localBoxStore: BoxStore,
            @Named("${Moidom.TAG}.${StreamingService.TV}") accountChangeSubject: UserAccountSubject
    )
    : TvBrowseCursorLocalStore = TvBrowseCursorLocalStoreDelegate(localBoxStore, accountChangeSubject)

    /**
     *  VOD Service Local Store
     */
    @Named("${Moidom.TAG}.${StreamingService.VOD}")
    @Singleton @Provides fun provideVodServiceLocalStore(
        @Named("${Moidom.TAG}.${StreamingService.VOD}") localBoxStore: BoxStore,
        @Named("${Moidom.TAG}.${StreamingService.TV}") accountSubject: UserAccountSubject
    ): VodDirectoryLocalStore = VodDirectoryLocalStoreDelegate(localBoxStore, accountSubject)

    /**
     *  VOD Service Remote Store
     */
    @Named("${Moidom.TAG}.${StreamingService.VOD}")
    @Singleton @Provides fun provideVodServiceRemoteStore(
        remoteService: RestServiceMoidom, remoteSession: RemoteSessionRepositoryMoidom
    ): VodDirectoryRemoteStore = VodDirectoryRemoteStoreMoiDom(remoteService, remoteSession)

    /**     VOD Play Cursor, Local Store
     */
    @Named("${Moidom.TAG}.${StreamingService.VOD}")
    @Singleton @Provides fun provideVodPlayCursorLocalStoreMoidomDelegate(
        @Named("${Moidom.TAG}.${StreamingService.VOD}") localBoxStore: BoxStore,
        @Named("${Moidom.TAG}.${StreamingService.TV}") accountChangeSubject: UserAccountSubject // VOD Service uses the same account here as TV Service
    )
    : VodPlayCursorLocalStore = VodPlayCursorLocalStoreDelegate(localBoxStore, accountChangeSubject)

    /**     VOD Browsing Cursor, Local Store
     */
    @Named("${Moidom.TAG}.${StreamingService.VOD}")
    @Singleton @Provides fun provideVodBrowseCursorLocalStoreMoidomDelegate(
        @Named("${Moidom.TAG}.${StreamingService.VOD}") localBoxStore: BoxStore,
        @Named("${Moidom.TAG}.${StreamingService.TV}") accountChangeSubject: UserAccountSubject // VOD Service uses the same account here as TV Service
    )
    : VodBrowseCursorLocalStore = VodBrowseCursorLocalStoreDelegate(localBoxStore, accountChangeSubject)


    /**     Login Event Subject
     */
    @Singleton @Provides fun provideLoginEventSubject(): PublishSubject<LoginEvent>
            = PublishSubject.create()

    /**     Account change notification pipe. Like "Login Event Subject", but for Local module
     */
    @Singleton
    @Provides
    @Named("${Moidom.TAG}.${StreamingService.TV}")
    fun provideAccountChangeSubject(): UserAccountSubject = UserAccountSubject.create()
}