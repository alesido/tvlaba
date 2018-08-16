package org.alsi.android.datatv.store

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory

/** Contract for remote data store of a TV Streaming Service.
 *
 * Implementation requirements:
 *
 * - remote data store should give list of categories in the order agreed, -
 * hardcoded or received from server;
 *
 * - remote data store may give a logo icon reference with each category entity; all the
 * references should be of the same type;
 *
 */
interface TvChannelRemoteStore
{
    /** Load data both categories and channels
     */
    fun getDirectory(): Single<TvChannelDirectory>

    /** Load data of all TV channel categories from a remote data store of a TV video streaming service
     */
    fun getCategories(): Single<List<TvChannelCategory>>

    /** Load data of all TV channels
     */
    fun getChannels(): Single<List<TvChannel>>

    /** Load data of a channels subset
     */
    fun getChannels(channelIds: List<Long>): Single<List<TvChannel>>

    // endregion
    // region Favorite Channels

    fun addChannelToFavorites(channelId: Long): Completable
    fun removeChannelFromFavorites(channelId: Long): Completable
    fun toggleChannelToBeFavorite(channelId: Long): Completable

    // endregion
    // region Feature

    fun hasFeature(feature: TvChannelRemoteStoreFeature): Single<Boolean>

    // endregion
}