package org.alsi.android.datatv.store

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.domain.context.model.ServiceRepository
import org.alsi.android.domain.context.model.ServiceSession
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.repository.guide.TvChannelRepository
import javax.inject.Inject

class TvChannelDataRepository @Inject constructor(
        private val remote: TvChannelRemoteStore,
        private val local: TvChannelLocalStore)
    : TvChannelRepository
{
    /** Reconfigure data store to continue with another session data
     *
     */
    override fun withSession(session: ServiceSession): ServiceRepository {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /** Preload all frequently accessed data to the database
     *
     *  Assumed that the categories and channels are independent record sets, so it isn't of a big
     *  importance in which order they loaded and stored
     */
    override fun preload(): Completable {
        return remote.getCategories().flatMap { local.putCategories(it)
            remote.getChannels() }.flatMapCompletable { local.putChannels(it) }
    }

    // region Categories

    override fun getCategories(): Observable<List<TvChannelCategory>> = local.getCategories()

    override fun findCategoryById(categoryId: Long): Single<TvChannelCategory> = local.findCategoryById(categoryId)

    // endregion
    // region Channels

    override fun getChannels(categoryId: Long): Observable<List<TvChannel>> = local.getChannels(categoryId)

    override fun findChannelByNumber(channelNumber: Int): Single<TvChannel> = local.findChannelByNumber(channelNumber)

    /** ... not clear how to notify view model on the update completion, via remembered channels list observable?
     * depends on whether domain logic will manage updates
     */
    override fun getChannelsUpdate(channelIds: List<Long>): Completable {
        return remote.getChannels(channelIds).flatMapCompletable { local.updateChannels(it) }
    }

    // endregion
    // region Favorites

    override fun addChannelToFavorites(channelId: Long): Completable {
        return doFavoriteChannelOperation(channelId, {remote.addChannelToFavorites(channelId)},
                {local.addChannelToFavorites(channelId)})
    }

    override fun removeChannelFromFavorites(channelId: Long): Completable {
        return doFavoriteChannelOperation(channelId, {remote.removeChannelFromFavorites(channelId)},
                {local.removeChannelFromFavorites(channelId)})
    }

    override fun toggleChannelToBeFavorite(channelId: Long): Completable {
        return doFavoriteChannelOperation(channelId, {remote.toggleChannelToBeFavorite(channelId)},
                {local.toggleChannelFromFavorites(channelId)})
    }

    private fun doFavoriteChannelOperation(channelId: Long, remoteAction: (channelId: Long) -> Completable,
            localAction: (channelId: Long) -> Completable ): Completable {
        return remote.hasFeature(TvChannelRemoteStoreFeature.FAVORITE_CHANNEL)
                .flatMap { if (it) remoteAction(channelId); Single.just(it) }
                .flatMapCompletable { localAction(channelId) }
    }

    override fun isChannelFavorite(channelId: Long): Single<Boolean> = local.isChannelFavorite(channelId)

    // endregion
}