package org.alsi.android.datatv.store

import io.reactivex.Completable
import io.reactivex.Single
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

    /**
     */
    fun getLastPlayCursor(): Single<TvPlayCursor?>

    /**
     */
    fun getPlayHistory(): Single<List<TvPlayCursor>?>
}