package org.alsi.android.datatv.store

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory

/**
 * Created on 7/12/18.
 */
interface TvChannelLocalStore {

    fun switchUser(userLoginName: String)

    fun putDirectory(directory: TvChannelDirectory): Completable
    fun getDirectory(): Single<TvChannelDirectory>


    // region Categories

    /** ... replace all categories if exist
     */
    fun putCategories(categories: List<TvChannelCategory>): Completable
    fun getCategories(): Single<List<TvChannelCategory>>
    fun findCategoryById(categoryId: Long): Single<TvChannelCategory?>

    // endregion
    // region Channels

    fun putChannels(channels: List<TvChannel>): Completable
    fun getChannels(): Single<List<TvChannel>>
    fun getChannels(categoryId: Long): Single<List<TvChannel>>

    fun findChannelByNumber(channelNumber: Int): Single<TvChannel?>

    fun getChannelWindowExpirationMillis(channelIds: List<Long>): Long?

    // endregion
    // region Favorites

    fun addChannelToFavorites(channelId: Long): Completable
    fun removeChannelFromFavorites(channelId: Long): Completable
    fun isChannelFavorite(channelId: Long): Single<Boolean>
    fun toggleChannelFromFavorites(channelId: Long): Completable
    fun getFavoriteChannels(): Single<List<TvChannel>>

    // endregion
}