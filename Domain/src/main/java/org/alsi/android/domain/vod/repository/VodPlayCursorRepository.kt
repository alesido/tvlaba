package org.alsi.android.domain.vod.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.domain.vod.model.session.VodPlayCursor

abstract class VodPlayCursorRepository {

    protected var cursor = VodPlayCursor.empty()

    /** Set cursor immediately, store synchronously.
     *
     * This method allows to change each cursor part or a subset of them independently at once.
     */
    fun setCursorTo(
        playback: VodPlayback,
        seekTime: Long = 0,
        timeStamp: Long = System.currentTimeMillis()
    ) : Single<VodPlayback> {
        val previousCursor = cursor
        cursor = VodPlayCursor(playback, timeStamp, seekTime)
        return finalizeCursorSetting(previousCursor)
    }

    /** Store current cursor value. It is reasonable to store a few last cursor values to
     * have playback history.
     */
    protected abstract fun finalizeCursorSetting(previousCursor: VodPlayCursor): Single<VodPlayback>

    /** Update [seek position in] cursor.
     */
    abstract fun updateCursor(playback: VodPlayback): Completable

    /** Subscribe to updates on current playback. Implementation should get actual URI of the stream from the
     * remote or get it cached from the local store.
     */
    abstract fun current(): Observable<VodPlayback>

    /** Get last cursor set.
     */
    abstract fun last(): Single<VodPlayCursor?>

    /** Get data on latest cursor record over all users
     */
    abstract fun mostRecent(serviceId: Long): Single<UserActivityRecord?>

    /** Get playback history. Returns all the history stored.
     */
    abstract fun history(): Single<List<VodPlayCursor>?>
}