package org.alsi.android.moidom.store.internal

import io.objectbox.BoxStore
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.moidom.ContextMoidom
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.model.local.user.UserAccountEntity
import org.alsi.android.moidom.model.local.user.UserAccountEntity_
import org.alsi.android.moidom.model.remote.LoginResponse
import org.alsi.android.moidom.store.internal.mapper.AccountSourceDataMapper
import javax.inject.Inject
import javax.inject.Named

class InternalStoreMoidom @Inject constructor(

        @Named(Moidom.INTERNAL_STORE_NAME) private val boxStore: BoxStore,
        private val context: ContextMoidom) {

    /** TODO Map account, session, settings data from response to local database
     */
    fun importLoginResponseData(source: LoginResponse, loginName: String, loginPassword: String): Completable {
        if (!importAccount(source, loginName, loginPassword)) {
            return Completable.complete() // <-- TODO return domain level error
        }
        return Completable.complete()
    }

    private fun importAccount(source: LoginResponse, loginName: String, loginPassword: String): Boolean
    {
        val accountImport = AccountSourceDataMapper(loginName, loginPassword).mapFromSource(source)
        val accountBox = boxStore.boxFor(UserAccountEntity::class.java)
        val accountStored = accountBox.query().equal(UserAccountEntity_.loginName, loginName).build().findUnique()
        if (accountStored != null) {
            accountImport.id = accountStored.id
        }
        context.userAccountId = accountBox.put(accountImport)
        return true
    }

    fun getAccount(): Single<UserAccountEntity> {
        return Single.just(boxStore.boxFor(UserAccountEntity::class.java).get(context.userAccountId))
    }
}