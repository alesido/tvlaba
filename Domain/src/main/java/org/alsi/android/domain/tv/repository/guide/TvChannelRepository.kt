package org.alsi.android.domain.tv.repository.guide

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.domain.context.model.ServiceRepository
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory

/** Contract on TV repository of TV channels. Separated from the TV programs repository
 * to simplify it and allow for applications w/o programs.
 *
 * Created on 7/3/18.
 */

@Suppress("unused")
interface TvChannelRepository : ServiceRepository
{
    // region Categories

    fun getCategories(): Observable<List<TvChannelCategory>>

    fun findCategoryById(categoryId: Long): Single<TvChannelCategory>

    // endregion
    // region Channels

    fun getChannels(categoryId: Long): Observable<List<TvChannel>>

    fun getChannelsUpdate(channelIds: List<Long>): Completable // assumed that the subscribed views will receive updates
    fun findChannelByNumber(channelNumber: Int): Single<TvChannel>

    // endregion
    // region Favorite Channels

    fun addChannelToFavorites(channelId: Long): Completable
    fun removeChannelFromFavorites(channelId: Long): Completable
    fun toggleChannelToBeFavorite(channelId: Long): Completable
    fun isChannelFavorite(channelId: Long): Single<Boolean>

    // endregion

    fun preload(): Completable
}