package org.alsi.android.data.repository.account

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.UserAccount

/** Local synchronized copy of a user account of a streaming service user.
 */
interface AccountDataLocal
{
    fun addAttachAccount(account: UserAccount)
    fun attachAccountFor(loginName: String): UserAccount

    fun getLoginName(): Single<String>
    fun getPassword(): Single<String>
    fun getPassword(loginName: String): Single<String>

    fun getSubscriptions(): Single<List<ServiceSubscription>>
    fun setSubscriptions(subscriptions: List<ServiceSubscription>): Completable
}