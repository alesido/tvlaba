package org.alsi.android.datatv.repository

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
        return local.putPlayCursor(cursor).andThen(
            local.getLastPlayCursor()
        ).flatMap { //TODO Compare memory and stored cursor and give an error if they do not match
//            cursor = storedCursor
//            currentPlaybackSubject.onNext(storedCursor.playback)
            currentPlaybackSubject.onNext(cursor.playback)
            Single.just(cursor.playback)
        }.doOnError {
            if (previousCursor != null)
                cursor = previousCursor
        }
    }


    /** Subscribe to cursor updates
     */
    override fun current(): Observable<TvPlayback> = currentPlaybackSubject

    override fun last(): Single<TvPlayCursor?> = local.getLastPlayCursor()

    override fun latest(): Single<UserActivityRecord?> = local.getMostRecentActivity()

    override fun history(): Single<List<TvPlayCursor>?> {
        return local.getPlayHistory()
    }
}