package org.alsi.android.moidom

import android.content.Context
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datatv.repository.TvVideoStreamDataRepository
import org.alsi.android.datatv.store.*
import org.alsi.android.domain.streaming.model.ServiceProvider
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.tv.repository.guide.TvVideoStreamRepository
import org.alsi.android.local.store.tv.TvChannelLocalStoreDelegate
import org.alsi.android.local.store.tv.TvPlayCursorLocalStoreDelegate
import org.alsi.android.local.store.tv.TvProgramLocalMemoryStoreDelegate
import org.alsi.android.local.store.tv.TvVideoStreamLocalStoreDelegate
import org.alsi.android.moidom.Moidom.INTERNAL_STORE_NAME
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

    /**     MoiDom VOD service
     */
    @Singleton @Provides fun provideVodServiceMoidom(
            @Named("${Moidom.TAG}.${StreamingService.VOD}") serviceId: Long)
            = VodServiceMoidom(serviceId)

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
    @Singleton @Provides fun provideRestServiceMoiDom() = DataServiceFactoryMoidom.makeRestServiceMoidom()

    /**     Moidom service-wide Local Store for TV date
     */
    @Singleton @Provides @Named(INTERNAL_STORE_NAME)
    fun provideInternalStoreMoidom(context: Context): BoxStore
            = MyObjectBox.builder().name(INTERNAL_STORE_NAME).androidContext(context).build()

    /**     Moidom service-wide Local Store for TV date
     */
    @Named("${Moidom.TAG}.${StreamingService.TV}")
    @Singleton @Provides fun provideTvServiceLocalStoreMoidom(
            context: Context,
            @Named("${Moidom.TAG}.${StreamingService.TV}") serviceId: Long): BoxStore
            = org.alsi.android.local.model.MyObjectBox.builder().name("${Moidom.TAG}.${StreamingService.TV}.$serviceId")
            .androidContext(context).build()

    /**     TV Channel Remote Store
     */
    @Singleton @Provides fun provideTvChannelRemoteStoreMoidom(
            remoteService: RestServiceMoidom, remoteSession: RemoteSessionRepositoryMoidom)
    : TvChannelRemoteStore = TvChannelRemoteStoreMoidom(remoteService, remoteSession)

    /**     TV Channel Local Store
     */
    @Singleton @Provides fun provideTvChannelLocalStoreMoidomDelegate(
            @Named("${Moidom.TAG}.${StreamingService.TV}") localBoxStore: BoxStore)
    : TvChannelLocalStore = TvChannelLocalStoreDelegate(localBoxStore, "guest")

    /**     TV Program Remote Store
     */
    @Singleton @Provides fun provideTvProgramRemoteStoreMoidom(
            remoteService: RestServiceMoidom, remoteSession: RemoteSessionRepositoryMoidom)
    : TvProgramRemoteStore = TvProgramRemoteStoreMoidom(remoteService, remoteSession)

    /**     TV Program Local Store
     */
    @Singleton @Provides fun provideTvProgramLocalStoreMoidom()
    : TvProgramLocalStore = TvProgramLocalMemoryStoreDelegate()

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

    /**     TV Playback Cursor Local Store
     */
    @Named("${Moidom.TAG}.${StreamingService.TV}")
    @Singleton @Provides fun provideTvPlayCursorLocalStoreMoidomDelegate(
            @Named("${Moidom.TAG}.${StreamingService.TV}") localBoxStore: BoxStore)
    : TvPlayCursorLocalStore = TvPlayCursorLocalStoreDelegate(localBoxStore, "guest")

    /**     Login Event subject
     */
    @Singleton @Provides fun provideLoginEventSubject(): PublishSubject<LoginEvent>
            = PublishSubject.create()

}