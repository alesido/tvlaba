package org.alsi.android.data.repository.account

import io.reactivex.Single
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.domain.user.repository.AccountDataService

/**
 *  This is a gateway between higher level account data service/repository and
 *  concrete service(s)  for authentication, authorization and user
 *  account data operations.
 */
open class AccountDataGateway(
        
        open val remote: AccountDataRemote,
        open val local: AccountDataLocal)

    : AccountDataService {

    override fun login(loginName: String, loginPassword: String): Single<UserAccount>
    = remote.login(loginName, loginPassword).map {
        local.addAttachAccount(it)
        remote.notifyOnLogin()
        it
    }

    /** Resuming previous session
     */
    override fun resume(loginName: String, skipRemoteLogin: Boolean): Single<UserAccount> {
        return if (skipRemoteLogin)
            remote.onLoginResume( local.attachAccountFor(loginName) )
        else
            local.getPassword(loginName).flatMap { remote.login(loginName, it) }
                .map { local.addAttachAccount(it); it }
    }

    override fun getLoginName() = local.getLoginName()
    override fun getPassword() = local.getPassword()

    override fun getSubscriptions() = local.getSubscriptions()
}