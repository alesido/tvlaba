package org.alsi.android.domain.tv.repository.guide

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvChannelListWindow

/** Contract on TV repository of TV channels. Separated from the TV programs repository
 * to simplify it and allow for applications w/o programs.
 *
 * Created on 7/3/18.
 */

@Suppress("unused")
interface TvChannelRepository
{
    // region Directory
    fun observeDirectory(): Observable<TvChannelDirectory>
    fun getDirectory(): Single<TvChannelDirectory>

    // endregion
    // region Categories

    fun getCategories(): Observable<List<TvChannelCategory>>
    fun findCategoryById(categoryId: Long): Single<TvChannelCategory?>

    // endregion
    // region Channels

    fun getChannels(categoryId: Long): Observable<List<TvChannel>>
    fun findChannelById(channelId: Long): Single<TvChannel?>
    fun findChannelByNumber(channelNumber: Int): Single<TvChannel?>

    // endregion
    // region Actualization

    fun getChannelsVisibilitySubject(): PublishSubject<TvChannelListWindow>
    fun scheduleChannelsUpdate(window: TvChannelListWindow, cancelUpdate: Boolean = false)

    // endregion
    // region Favorite Channels

    fun addChannelToFavorites(channelId: Long): Completable
    fun removeChannelFromFavorites(channelId: Long): Completable
    fun toggleChannelToBeFavorite(channelId: Long): Completable
    fun isChannelFavorite(channelId: Long): Single<Boolean>

    // endregion
    // region Configuration

    /** Reload language dependent parts of the directory to local store if supported
     */
    fun onLanguageChange(): Completable

    /** Update time shift dependent parts of the directory to local store if supported
     */
    fun onTimeShiftChange(): Completable

    // endregion
    // region Authorization

    /** Authorize access to all channels protected by parental password or to a particular
     *  channel given by the 2nd optional parameter.
     */
    fun authorizeContentAccess(password: String, channelId: Long? = null): Completable

    // endregion
}