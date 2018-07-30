package org.alsi.android.moidom.store.internal

import io.objectbox.BoxStore
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationContext
import org.alsi.android.domain.streaming.model.ServiceProvider
import org.alsi.android.moidom.model.local.user.UserAccountEntity
import org.alsi.android.moidom.model.local.user.UserAccountEntity_
import org.alsi.android.moidom.model.remote.LoginResponse
import org.alsi.android.moidom.store.internal.mapper.AccountSourceDataMapper
import javax.inject.Inject

class InternalStoreMoidom @Inject constructor(
        private val boxStore: BoxStore,
        private val serviceProvider: ServiceProvider,
        private val presentationContext: PresentationContext) {

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
        val accountImport = AccountSourceDataMapper(loginName, loginPassword, serviceProvider).mapFromSource(source)
        val accountBox = boxStore.boxFor(UserAccountEntity::class.java)
        val accountStored = accountBox.query().equal(UserAccountEntity_.loginName, loginName).build().findUnique()
        if (null == accountStored) {
            accountBox.put(accountImport)
        }
        else {
            accountImport.id = accountStored.id
            if (accountImport != accountStored) accountBox.put(accountImport)
        }
        return true
    }

    fun getAccount(): Single<UserAccountEntity> {
        return Single.just(boxStore.boxFor(UserAccountEntity::class.java).get(presentationContext.account.id))
    }
}