package org.alsi.android.datatv.repository

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datatv.store.TvChannelLocalStore
import org.alsi.android.datatv.store.TvChannelRemoteStore
import org.alsi.android.datatv.store.TvChannelRemoteStoreFeature
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelListWindow
import org.alsi.android.domain.tv.repository.guide.TvChannelRepository

/** Repository for TV channels directory comprised of categories and channels accessed remotely
 *  and stored locally for frequent access (cache with querying).
 *
 *  NOTE Some server API's have just a single method to get both categories and channels (Novoe TV).
 *  Others have separate methods for that (Moi Dom TV). Some API's, like Kingmod, require
 *  channels and categories to be preloaded. It is better to take care about that at the API
 *  implementation level. Here it is hard to figure out the general case. Thus, - no preload
 *  methods, etc.
 */
abstract class TvChannelDataRepository: TvChannelRepository {

    open lateinit var remote: TvChannelRemoteStore
    open lateinit var local: TvChannelLocalStore

    private val visibilitySubject: PublishSubject<TvChannelListWindow> = PublishSubject.create()

    init {
        visibilitySubject.subscribe { window -> scheduleChannelsUpdate(window) }
    }

    // region Categories

    override fun findCategoryById(categoryId: Long): Single<TvChannelCategory?> = local.findCategoryById(categoryId)

    // endregion
    // region Channels

    override fun findChannelByNumber(channelNumber: Int): Single<TvChannel?> = local.findChannelByNumber(channelNumber)

    override fun getChannelsVisibilitySubject() = visibilitySubject

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