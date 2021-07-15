package org.alsi.android.domain.tv.repository.session

import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.session.TvPlayCursor

abstract class TvPlayCursorRepository {

    /** Current cursor value available immediately and stored asynchronously.
     */
    protected var cursor: TvPlayCursor = TvPlayCursor(
            categoryId = 0L,
            playback = TvPlayback(channelId = 0L, stream = null),
            seekTime = 0L,
            timeStamp = System.currentTimeMillis()
    )

    /** Set cursor immediately, store synchronously.
     *
     * This method allows to change each cursor part or a subset of them independently at once.
     */
    fun setCursorTo(

            categoryId: Long,
            playback: TvPlayback,
            seekTime: Long = 0

    ) : Single<TvPlayback> {

        val previousCursor = cursor
        cursor.let {
            it.categoryId = categoryId
            it.playback = playback
            it.seekTime = seekTime
            it.timeStamp = System.currentTimeMillis()
        }

        return finalizeCursorSetting(previousCursor)
    }

    /** Store current cursor value. It is reasonable to store a few last cursor values to
     * have playback history.
     */
    protected abstract fun finalizeCursorSetting(previousCursor: TvPlayCursor?): Single<TvPlayback>

    /** Subscribe to updates on current playback. Implementation should get actual URI of the stream from the
     * remote or get it cached from the local store.
     */
    abstract fun current(): Observable<TvPlayback>

    /** Get last cursor set.
     */
    abstract fun last(): Single<TvPlayCursor?>

    /** Get data on latest cursor record over all users
     */
    abstract fun latest(): Single<UserActivityRecord?>

    /** Get playback history. Returns all the history stored.
     */
    abstract fun history(): Single<List<TvPlayCursor>?>
}