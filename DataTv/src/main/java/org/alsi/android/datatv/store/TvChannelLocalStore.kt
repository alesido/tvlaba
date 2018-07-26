package org.alsi.android.datatv.store

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.domaintv.model.TvChannel
import org.alsi.android.domaintv.model.TvChannelCategory

/**
 * Created on 7/12/18.
 */
interface TvChannelLocalStore {

    // region Categories

    /** ... replace all categories if exist
     */
    fun putCategories(categories: List<TvChannelCategory>): Completable
    fun getCategories(): Observable<List<TvChannelCategory>>
    fun findCategoryById(categoryId: Long): Single<TvChannelCategory>

    // endregion
    // region Channels

    fun putChannels(channels: List<TvChannel>): Completable
    fun getChannels(): Observable<List<TvChannel>>
    fun getChannels(categoryId: Long): Observable<List<TvChannel>>
    fun updateChannels(it: List<TvChannel>): Completable
    fun findChannelByNumber(channelNumber: Int): Single<TvChannel>

    // endregion
    // region Favorites

    fun addChannelToFavorites(channelId: Long): Completable
    fun removeChannelFromFavorites(channelId: Long): Completable
    fun isChannelFavorite(channelId: Long): Single<Boolean>
    fun toggleChannelFromFavorites(channelId: Long): Completable

    // endregion
}