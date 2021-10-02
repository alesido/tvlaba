package org.alsi.android.datavod.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.alsi.android.datavod.store.VodPlayCursorLocalStore
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.domain.vod.model.session.VodPlayCursor
import org.alsi.android.domain.vod.repository.VodPlayCursorRepository

abstract class VodPlayCursorDataRepository(

        private val local: VodPlayCursorLocalStore

): VodPlayCursorRepository() {

    private val currentPlaybackSubject: BehaviorSubject<VodPlayback> = BehaviorSubject.create()

    /** Save current playback cursor to the storage asynchronously.
     */
    override fun finalizeCursorSetting(previousCursor: VodPlayCursor) : Single<VodPlayback> {
        currentPlaybackSubject.onNext(cursor.playback)
        return local.putPlayCursor(cursor).andThen (Single.just(cursor.playback))
            .doOnError { cursor = previousCursor }
    }

    override fun updateCursor(playback: VodPlayback): Completable = local.updatePlayCursor(playback)

    /** To Subscribe to cursor updates
     */
    override fun current(): Observable<VodPlayback> = currentPlaybackSubject

    override fun last(): Single<VodPlayCursor?> = local.getLastPlayCursor()

    override fun mostRecent(serviceId: Long): Single<UserActivityRecord?> = local.getMostRecentActivity(serviceId)

    override fun history(): Single<List<VodPlayCursor>?> {
        return local.getPlayHistory()
    }
}