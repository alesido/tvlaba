package org.alsi.android.moidom.store.tv

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.datatv.store.TvChannelRemoteStore
import org.alsi.android.datatv.store.TvChannelRemoteStoreFeature
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.moidom.mapper.TvCategoriesSourceDataMapper
import org.alsi.android.moidom.mapper.TvChannelDirectorySourceDataMapper
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvChannelRemoteStoreMoidom @Inject constructor(
        private val remoteService: RestServiceMoidom,
        private val remoteSession: RemoteSessionRepositoryMoidom
    )
    : TvChannelRemoteStore {

    private val timeZoneQueryParameter = DateTimeFormat.forPattern("ZZ")
    .withZone(DateTimeZone.getDefault()).print(0).replace(":", "")

    private val categoriesSourceMapper = TvCategoriesSourceDataMapper()
    private val channelsSourceMapper = TvChannelDirectorySourceDataMapper()

    override fun getDirectory(): Single<TvChannelDirectory> {
        return remoteSession.getSessionId()
                .flatMap { sid -> remoteService.getAllChannels(sid, timeZoneQueryParameter) }
                .map { response -> channelsSourceMapper.mapFromSource(response) }
    }

    override fun getCategories(): Single<List<TvChannelCategory>> {
        return remoteSession.getSessionId()
                .flatMap { sessionId -> remoteService.getGroups(sessionId) }
                .map { response -> categoriesSourceMapper.mapFromSource(response) }
    }

    override fun getChannels(): Single<List<TvChannel>> {
        return remoteSession.getSessionId()
                .flatMap { sid -> remoteService.getAllChannels(sid, timeZoneQueryParameter) }
                .map { response -> channelsSourceMapper.mapFromSource(response).channels }
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
}
