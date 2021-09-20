package org.alsi.android.local.store.vod

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datavod.store.VodBrowseCursorLocalStore
import org.alsi.android.domain.context.model.SessionActivityType
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.domain.vod.model.session.VodBrowseCursor
import org.alsi.android.domain.vod.model.session.VodBrowseCursorReference
import org.alsi.android.local.mapper.vod.VodBrowseCursorEntityMapper
import org.alsi.android.local.model.vod.VodBrowseCursorEntity
import org.alsi.android.local.model.vod.VodBrowseCursorEntity_

class VodBrowseCursorLocalStoreDelegate (

        serviceBoxStore: BoxStore,
        val loginSubject: PublishSubject<UserAccount>

) : VodBrowseCursorLocalStore
{
    private var userLoginName: String = "guest"

    private val cursorBox: Box<VodBrowseCursorEntity> = serviceBoxStore.boxFor()

    private val browseCursorMapper = VodBrowseCursorEntityMapper()

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
    override fun putBrowseCursor(cursor: VodBrowseCursor): Completable {
        return Completable.fromRunnable {

            // insert or update record for given user

            val record = cursorBox.query {
                equal(VodBrowseCursorEntity_.userLoginName, userLoginName)
            }.findFirst()

            val entity = browseCursorMapper.mapToEntity(cursor)
            record?.let { entity.id = it.id }
            entity.userLoginName = userLoginName
            cursorBox.put(entity)
        }
    }

    override fun getBrowseCursorReference(): Single<VodBrowseCursorReference?> = Single.fromCallable {
        val record = cursorBox.query {
            equal(VodBrowseCursorEntity_.userLoginName, userLoginName)
            orderDesc(VodBrowseCursorEntity_.timeStamp)
        }.findFirst()
        if (record != null)
            browseCursorMapper.mapFromEntityToReference(record)
        else
            VodBrowseCursorReference.empty()
    }

    override fun getMostRecentActivity(serviceId: Long): Single<UserActivityRecord?> = Single.fromCallable {
        val record = cursorBox.query {
            orderDesc(VodBrowseCursorEntity_.timeStamp)
        }.findFirst()
        if (record != null)
            UserActivityRecord(record.userLoginName, serviceId, SessionActivityType.BROWSING_VOD, record.timeStamp)
        else
            UserActivityRecord.empty()
    }


    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }
}