package org.alsi.android.datatv.repository

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.alsi.android.datatv.store.TvBrowseCursorLocalStore
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.repository.session.TvBrowseCursorRepository

open class TvBrowseCursorDataRepository(
    private val local: TvBrowseCursorLocalStore
): TvBrowseCursorRepository() {

    private val browsingSubject: BehaviorSubject<TvBrowseCursor> = BehaviorSubject.create()

    override fun finalizeCursorSetting(previousCursor: TvBrowseCursor?) : Single<TvBrowseCursor> {
        browsingSubject.onNext(cursor)
        return local.putBrowseCursor(cursor).andThen(Single.just(cursor))
    }

    override fun getCursor(): Single<TvBrowseCursor> = Single.just(cursor)

    override fun observeCursor(): Observable<TvBrowseCursor> = browsingSubject

    override fun mostRecent(serviceId: Long): Single<UserActivityRecord?> = local.getMostRecentActivity(serviceId)
}