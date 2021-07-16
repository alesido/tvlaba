package org.alsi.android.local.store.tv

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datatv.store.TvBrowseCursorLocalStore
import org.alsi.android.domain.context.model.SessionActivityType
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvBrowseCursorReference
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.mapper.TvBrowseCursorEntityMapper
import org.alsi.android.local.model.tv.TvBrowseCursorEntity
import org.alsi.android.local.model.tv.TvBrowseCursorEntity_

class TvBrowseCursorLocalStoreDelegate (

        serviceBoxStore: BoxStore,
        val loginSubject: PublishSubject<UserAccount>

) : TvBrowseCursorLocalStore
{
    private var userLoginName: String = "guest"

    private val cursorBox: Box<TvBrowseCursorEntity> = serviceBoxStore.boxFor()

    private val browseCursorMapper = TvBrowseCursorEntityMapper()

    private val disposables = CompositeDisposable()

    init {
        val s = loginSubject.subscribe {
            switchUser(it.loginName)
        }
        s?.let { disposables.add(it) }
    }

    override fun switchUser(userLoginName: String) {
        this.userLoginName = userLoginName
    }

    /** Insert or update browsing cursor record. No browsing history supported.
     */
    override fun putBrowseCursor(cursor: TvBrowseCursor): Completable {
        return Completable.fromRunnable {

            // insert or update record for given user

            val record = cursorBox.query {
                equal(TvBrowseCursorEntity_.userLoginName, userLoginName)
            }.findFirst()

            val entity = browseCursorMapper.mapToEntity(cursor)
            record?.let { entity.id = it.id }
            entity.userLoginName = userLoginName
            cursorBox.put(entity)
        }
    }

    override fun getBrowseCursorReference(): Single<TvBrowseCursorReference?>  = Single.fromCallable {
        val record = cursorBox.query {
            equal(TvBrowseCursorEntity_.userLoginName, userLoginName)
            orderDesc(TvBrowseCursorEntity_.timeStamp)
        }.findFirst()
        if (record != null)
            browseCursorMapper.mapFromEntityToReference(record)
        else
            TvBrowseCursorReference.empty()
    }

    override fun getMostRecentActivity(): Single<UserActivityRecord?> = Single.fromCallable {
        val record = cursorBox.query {
            orderDesc(TvBrowseCursorEntity_.timeStamp)
        }.findFirst()
        if (record != null)
            UserActivityRecord(record.userLoginName, -1L, SessionActivityType.BROWSING_TV, record.timeStamp)
        else
            UserActivityRecord.empty()
    }


    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }
}