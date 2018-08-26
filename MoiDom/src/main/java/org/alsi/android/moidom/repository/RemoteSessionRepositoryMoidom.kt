package org.alsi.android.moidom.repository

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.query
import io.objectbox.query.OrderFlags
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.alsi.android.moidom.model.LoginEvent
import org.alsi.android.moidom.model.tv.RemoteSessionEntityMoidom
import org.alsi.android.moidom.model.tv.RemoteSessionEntityMoidom_
import javax.inject.Inject
import javax.inject.Named

open class RemoteSessionRepositoryMoidom {

    @field:[Inject Named(org.alsi.android.moidom.Moidom.INTERNAL_STORE_NAME)]
    lateinit var store: BoxStore

    @Inject lateinit var loginSubject: PublishSubject<LoginEvent>

    private val box: Box<RemoteSessionEntityMoidom> by lazy { store.boxFor(RemoteSessionEntityMoidom::class.java) }

    private val loginName: String? by lazy { box.query {
        order(RemoteSessionEntityMoidom_.loginTimestampMillis, OrderFlags.DESCENDING)}.findFirst()?.loginName }

    init {
        loginSubject.subscribe {
            box.put(RemoteSessionEntityMoidom(0L, it.account.loginName, it.data.sid, System.currentTimeMillis()))
        }
    }

    open fun getSessionId(): Single<String> = Single.create { emitter ->
        val sessionId = box.query {
            equal(RemoteSessionEntityMoidom_.loginName, loginName?: LOGIN_NAME_GUEST)
        }.findFirst()?.sessionId
        if (sessionId != null) emitter.onSuccess(sessionId)
        else emitter.onError(Throwable(ERROR_NO_SESSION_ID_AVAILABLE))
    }

    fun getUserLoginName() = loginName

    fun getStartTimeStamp() = box.query {
        equal(RemoteSessionEntityMoidom_.loginName, loginName?: return null)
    }.findFirst()?.loginTimestampMillis

    companion object {
        const val LOGIN_NAME_GUEST = "guest"

        const val ERROR_NO_SESSION_ID_AVAILABLE = "No session ID available"
    }
}