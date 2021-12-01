package org.alsi.android.moidom.store.tv

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.alsi.android.datatv.store.TvChannelRemoteStore
import org.alsi.android.datatv.store.TvChannelRemoteStoreFeature
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.streaming.repository.SettingsRepository
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.user.model.SubscriptionPackage
import org.alsi.android.local.model.user.UserAccountSubject
import org.alsi.android.moidom.mapper.TvCategoriesSourceDataMapper
import org.alsi.android.moidom.mapper.TvChannelDirectorySourceDataMapper
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat


class TvChannelRemoteStoreMoidom (
    private val serviceId: Long,
    accountSubject: UserAccountSubject,
    private val remoteService: RestServiceMoidom,
    private val remoteSession: RemoteSessionRepositoryMoidom,
    private val settingsRepository: SettingsRepository,
    private val defaults: StreamingServiceDefaults
)
    : TvChannelRemoteStore {

    private lateinit var subscriptionPackage: SubscriptionPackage

    private val timeZoneQueryParameter = DateTimeFormat.forPattern("ZZ")
    .withZone(DateTimeZone.getDefault()).print(0).replace(":", "")

    private val categoriesSourceMapper = TvCategoriesSourceDataMapper()
    private val channelsSourceMapper = TvChannelDirectorySourceDataMapper()

    private val disposables = CompositeDisposable()

    private var dbgExtended: Int = 1

    init {
        val s = accountSubject.subscribe { userAccount ->
            this.subscriptionPackage = userAccount.subscriptions
                .first { serviceId == it.serviceId }.subscriptionPackage
        }
        s?.let { disposables.add(it) }
    }

    override fun getDirectory(): Single<TvChannelDirectory> {
        return remoteSession.getSessionId()
                .flatMap { sid ->
                    remoteService.getAllChannels(sid, timeZoneQueryParameter)
                }
                .map { response ->
                    channelsSourceMapper.mapFromSource(response, subscriptionPackage,
                        settingsRepository.lastValues(), defaults)
                }
    }

    override fun getCategories(): Single<List<TvChannelCategory>> {
        return remoteSession.getSessionId()
                .flatMap { sessionId -> remoteService.getGroups(sessionId) }
                .map { response -> categoriesSourceMapper.mapFromSource(response) }
    }

    override fun getChannels(): Single<List<TvChannel>> {
        //dbgExtended = if (dbgExtended == 0) 1 else 0
        return remoteSession.getSessionId()
                .flatMap { sid -> remoteService.getAllChannels(sid, timeZoneQueryParameter, dbgExtended) }
                .map { response -> channelsSourceMapper.mapFromSource(response, subscriptionPackage,
                    settingsRepository.lastValues(), defaults).channels }
    }

    override fun getChannels(channelIds: List<Long>): Single<List<TvChannel>> {
        return getChannels().map { channels -> channels.filter { it.id in channelIds } }
    }

    /** This service supports only local favorites storage. */
    override fun addChannelToFavorites(channelId: Long): Completable = Completable.error(NotImplementedError())


    /** This service supports only local favorites storage. */
    override fun removeChannelFromFavorites(channelId: Long): Completable = Completable.error(NotImplementedError())

    /** This service supports only local favorites storage. */
    override fun toggleChannelToBeFavorite(channelId: Long): Completable = Completable.error(NotImplementedError())

    /** This service supports only local favorites storage. */
    override fun hasFeature(feature: TvChannelRemoteStoreFeature): Single<Boolean>
    = Single.just( when(feature) {
        TvChannelRemoteStoreFeature.FAVORITE_CHANNEL -> false
        else -> false })

    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }
}
