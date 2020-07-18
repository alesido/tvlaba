package org.alsi.android.moidom

import android.content.Context
import dagger.Module
import dagger.Provides
import io.objectbox.BoxStore
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datatv.store.TvChannelLocalStore
import org.alsi.android.datatv.store.TvChannelRemoteStore
import org.alsi.android.domain.streaming.model.ServiceProvider
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.local.Local
import org.alsi.android.local.store.AccountStoreLocalDelegate
import org.alsi.android.local.store.tv.TvChannelLocalStoreDelegate
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
import javax.inject.Named
import javax.inject.Singleton

@Module
class MoidomModule {

    /**
     * check for MutableList explanation @ https://stackoverflow.com/questions/45384389/why-cant-dagger-process-these-kotlin-generics
    */
    @Singleton @Provides @Named(Moidom.TAG) fun provideServicesMoidom(
            tvServiceMoiDom: TvServiceMoidom, vodServiceMoidom: VodServiceMoidom)
            : MutableList<StreamingService> = mutableListOf(tvServiceMoiDom, vodServiceMoidom)

    @Singleton @Provides fun provideServiceProviderMoidom(
            @Named(Moidom.TAG) id: Long,
            @Named(Moidom.TAG) name: String,
            accountService: AccountDataServiceMoidom,
            settingsRepository: SettingsRepositoryMoidom,
            @Named(Moidom.TAG) services: MutableList<StreamingService>)
            : ServiceProvider
            = ServiceProviderMoidom(id, name, accountService, settingsRepository, services)

    @Singleton @Provides fun provideRestServiceMoiDom() = DataServiceFactoryMoidom.makeRestServiceMoidom()

    @Singleton @Provides fun provideLoginEventSubject(): PublishSubject<LoginEvent>
            = PublishSubject.create()

    @Singleton @Provides @Named(INTERNAL_STORE_NAME)
    fun provideInternalStoreMoidom(context: Context): BoxStore
            = MyObjectBox.builder().name(INTERNAL_STORE_NAME).androidContext(context).build()

    @Singleton @Provides fun provideTvChannelRemoteStore(
            remoteService: RestServiceMoidom, remoteSession: RemoteSessionRepositoryMoidom)
            : TvChannelRemoteStore = TvChannelRemoteStoreMoidom(remoteService, remoteSession)

    @Named("${Moidom.TAG}.${StreamingService.TV}")
    @Singleton @Provides fun provideTvServiceLocalStoreMoidom(
            context: Context,
            @Named("${Moidom.TAG}.${StreamingService.TV}") serviceId: Long): BoxStore
            = org.alsi.android.local.model.MyObjectBox.builder().name("${Moidom.TAG}.${StreamingService.TV}.$serviceId")
            .androidContext(context).build()

    @Singleton @Provides fun provideTvServiceLocalStoreMoidomDelegate(
            @Named("${Moidom.TAG}.${StreamingService.TV}") localBoxStore: BoxStore)
    : TvChannelLocalStore = TvChannelLocalStoreDelegate(localBoxStore, "guest")

    @Singleton @Provides fun provideVodServiceMoidom(
            @Named("${Moidom.TAG}.${StreamingService.VOD}") serviceId: Long)
            = VodServiceMoidom(serviceId)
}