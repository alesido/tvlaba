package org.alsi.android.datavod.repository

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import org.alsi.android.datavod.store.VodBrowseCursorLocalStore
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.vod.model.session.VodBrowseCursor
import org.alsi.android.domain.vod.model.session.VodBrowseCursorReference
import org.alsi.android.domain.vod.repository.VodBrowseCursorRepository

open class VodBrowseCursorDataRepository(
    private val local: VodBrowseCursorLocalStore
): VodBrowseCursorRepository() {

    private val browsingSubject: BehaviorSubject<VodBrowseCursor> = BehaviorSubject.create()

    override fun finalizeCursorSetting(previousCursor: VodBrowseCursor?) : Single<VodBrowseCursor> {
        browsingSubject.onNext(cursor)
        return local.putBrowseCursor(cursor).andThen(Single.just(cursor))
    }

    override fun getCursor(): Single<VodBrowseCursor> = Single.just(cursor)

    override fun getStoredCursorReference(): Single<VodBrowseCursorReference?> = local.getBrowseCursorReference()

    override fun observeCursor(): Observable<VodBrowseCursor> = browsingSubject

    override fun mostRecent(serviceId: Long): Single<UserActivityRecord?> = local.getMostRecentActivity(serviceId)
}