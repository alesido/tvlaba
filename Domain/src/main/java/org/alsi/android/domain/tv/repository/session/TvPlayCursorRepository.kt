package org.alsi.android.domain.tv.repository.session

import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.session.TvPlayCursor

abstract class TvPlayCursorRepository {

    /** Current cursor value available immediately and stored asynchronously.
     */
    protected var cursor: TvPlayCursor = TvPlayCursor(
            categoryId = 0L,
            playback = TvPlayback(channelId = 0L, streamUri = null),
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

    /** Get current playback. Implementation should get actual URI of the stream from the
     * remote or get it cached from the local store.
     */
    abstract fun current(): Observable<TvPlayback>

    /** Get playback history. Returns all the history stored.
     */
    abstract fun history(): Single<List<TvPlayback>>

    /** Set playback cursor to the next channel's live and return the playback data.
     */
    abstract fun nextChannel(): Single<TvPlayback>

    /** Set playback cursor to the previous channel's live and return the playback data.
     */
    abstract fun prevChannel(): Single<TvPlayback>

    /** Set playback cursor to the next archive item and return the next playback data.
     */
    abstract fun nextProgram(): Single<TvPlayback>

    /** Set playback cursor to the previous archive item and return the next playback data.
     */
    abstract fun prevProgram(): Single<TvPlayback>
}