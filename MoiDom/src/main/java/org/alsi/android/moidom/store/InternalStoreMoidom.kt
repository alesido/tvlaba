package org.alsi.android.moidom.store

import io.objectbox.BoxStore
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.moidom.model.local.user.UserAccountEntity
import org.alsi.android.moidom.model.remote.LoginResponse

class InternalStoreMoidom(private val boxStore: BoxStore) {

    /** Map account, session, settings data from response to local database
     *
     */
    fun importLoginResponseData(source: LoginResponse, loginName: String, loginPassword: String): Completable {
        if (!importAccount(source, loginName, loginPassword)) {
            return Completable.complete() // <-- return domain level error
        }
        return Completable.complete()
    }

    private fun importAccount(source: LoginResponse, loginName: String, loginPassword: String): Boolean {
        val accountBox = boxStore.boxFor(UserAccountEntity::class.java)

        accountBox.query().equal(UserAccountEntity_.loginName, loginName).build().findUnique()?.let {
            return false
        }


        //UserAccountEntity(source: LoginResponse, loginName: String, loginPassword: String)

    }


    fun getAccount(): Single<UserAccountEntity> {
        return Single.just(null)
    }
}