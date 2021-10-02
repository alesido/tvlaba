package org.alsi.android.datavod.store

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.domain.vod.model.session.VodPlayCursor

interface VodPlayCursorLocalStore {

    /** Attach the store interface to store of another user.
     */
    fun switchUser(userLoginName: String)

    /** Insert or update playback cursor record.
     *
     * Ensure we keep only a part of the accumulated playback history. I.e., this method triggers
     * history clean up procedure. It seems that it's better to remove records older than a day
     * o two than keep last N records.
     */
    fun putPlayCursor(cursor: VodPlayCursor): Completable

    /**
     */
    fun updatePlayCursor(currentPlayback: VodPlayback): Completable

    /** Get latest play cursor position for current user
     */
    fun getLastPlayCursor(): Single<VodPlayCursor?>

    /** Get latest play cursor position over all users
     */
    fun getMostRecentActivity(serviceId: Long): Single<UserActivityRecord?>

    /**
     */
    fun getPlayHistory(): Single<List<VodPlayCursor>?>
}