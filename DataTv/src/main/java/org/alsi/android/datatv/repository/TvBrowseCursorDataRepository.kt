package org.alsi.android.datatv.repository

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.repository.session.TvBrowseCursorRepository

open class TvBrowseCursorDataRepository: TvBrowseCursorRepository() {

    private val currentPlaybackSubject: BehaviorSubject<TvBrowseCursor> = BehaviorSubject.create()

    override fun finalizeCursorSetting(previousCursor: TvBrowseCursor?) : Single<TvBrowseCursor> {
        // TODO Implement a local store for the Browse Cursor
        currentPlaybackSubject.onNext(cursor)
        return Single.just(cursor)
    }

    override fun getCursor(): Single<TvBrowseCursor> = Single.just(cursor)

    override fun observeCursor(): Observable<TvBrowseCursor> = currentPlaybackSubject
}