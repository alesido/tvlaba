package org.alsi.android.moidom.mapper

import org.alsi.android.local.model.user.UserAccountEntity
import org.alsi.android.local.store.AccountStoreLocalDelegate
import org.alsi.android.moidom.ContextMoidom
import org.alsi.android.moidom.model.LoginResponse
import javax.inject.Inject

class LoginResponseMapper @Inject constructor(

        private val localAccountStoreDelegate: AccountStoreLocalDelegate,
        private val context: ContextMoidom) {

    /** TODO Map account, session, settings data from response to local database
     */
    fun importLoginResponseData(source: LoginResponse, loginName: String, loginPassword: String): UserAccountEntity {
        return importAccount(source, loginName, loginPassword)
    }

    private fun importAccount(source: LoginResponse, loginName: String, loginPassword: String): UserAccountEntity
    {
        val account = AccountSourceDataMapper(loginName, loginPassword).mapFromSource(source)
        localAccountStoreDelegate.attach(account)
        context.userAccountId = account.id
        return account
    }
}