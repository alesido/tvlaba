package org.alsi.android.datatv.store

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.tv.model.session.TvPlayCursor

interface TvPlayCursorLocalStore {

    /** Attach the store interface to store of another user.
     */
    fun switchUser(userLoginName: String)

    /** Insert or update playback cursor record.
     *
     * Ensure we keep only a part of the accumulated playback history. I.e., this method triggers
     * history clean up procedure. It seems that it's better to remove records older than a day
     * o two than keep last N records.
     */
    fun putPlayCursor(cursor: TvPlayCursor): Completable

    /** Get latest play cursor position for current user
     */
    fun getLastPlayCursor(): Single<TvPlayCursor?>

    /** Get latest play cursor position over all users
     */
    fun getMostRecentActivity(): Single<UserActivityRecord?>

    /**
     */
    fun getPlayHistory(): Single<List<TvPlayCursor>?>
}