package org.alsi.android.datatv.store

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvChannelsChange
import org.alsi.android.domain.user.model.UserAccount

/**
 * Created on 7/12/18.
 */
interface TvChannelLocalStore {

    fun switchUser(userAccount: UserAccount)

    fun putDirectory(source: TvChannelDirectory): Completable
    fun getDirectory(): Single<TvChannelDirectory>
    fun setLanguage(languageCode: String): Completable
    fun setTimeShift(hours: Int): Completable

    // region Categories

    /** ... replace all categories if exist
     */
    fun updateCategories(categories: List<TvChannelCategory>): Completable
    fun getCategories(): Single<List<TvChannelCategory>>
    fun findCategoryById(categoryId: Long): Single<TvChannelCategory?>

    // endregion
    // region Channels

    fun updateChannels(channels: List<TvChannel>): Completable
    fun updateChannels(change: TvChannelsChange)
    fun getChannels(): Single<List<TvChannel>>
    fun getChannels(categoryId: Long): Single<List<TvChannel>>

    fun findChannelById(channelId: Long): Single<TvChannel?>
    fun findChannelByNumber(channelNumber: Int): Single<TvChannel?>

    fun getChannelWindowExpirationMillis(channelIds: List<Long>): Long?
    fun getChannelWindowUpdateSchedule(channelIds: List<Long>): List<Long>

    // endregion
    // region Favorites

    fun addChannelToFavorites(channelId: Long): Completable
    fun removeChannelFromFavorites(channelId: Long): Completable
    fun isChannelFavorite(channelId: Long): Single<Boolean>
    fun toggleChannelFromFavorites(channelId: Long): Completable
    fun getFavoriteChannels(): Single<List<TvChannel>>

    // endregion
}