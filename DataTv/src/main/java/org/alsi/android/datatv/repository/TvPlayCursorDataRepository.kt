package org.alsi.android.datatv.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.alsi.android.datatv.store.TvPlayCursorLocalStore
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.session.TvPlayCursor
import org.alsi.android.domain.tv.repository.session.TvPlayCursorRepository

abstract class TvPlayCursorDataRepository(

        private val local: TvPlayCursorLocalStore

): TvPlayCursorRepository() {

    private val currentPlaybackSubject: BehaviorSubject<TvPlayback> = BehaviorSubject.create()

    /** Save current playback cursor to the storage asynchronously.
     */
    override fun finalizeCursorSetting(previousCursor: TvPlayCursor?) : Single<TvPlayback> {
        currentPlaybackSubject.onNext(cursor.playback)
        return local.putPlayCursor(cursor).andThen (
            Single.just(cursor.playback)
        )
        .doOnError {
            if (previousCursor != null)
                cursor = previousCursor
        }
    }

    override fun updateCursor(playback: TvPlayback): Completable = local.updatePlayCursor(playback)

    /** To Subscribe to cursor updates
     */
    override fun current(): Observable<TvPlayback> = currentPlaybackSubject

    override fun last(): Single<TvPlayCursor?> = local.getLastPlayCursor()

    override fun mostRecent(serviceId: Long): Single<UserActivityRecord?> = local.getMostRecentActivity(serviceId)

    override fun history(): Single<List<TvPlayCursor>?> {
        return local.getPlayHistory()
    }
}