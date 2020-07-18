package org.alsi.android.domain.user.repository

import io.reactivex.Single
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.UserAccount

interface AccountDataService
{
    fun login(loginName: String, loginPassword: String): Single<UserAccount>

    fun getLoginName(): Single<String>
    fun getPassword(): Single<String>

    fun getSubscriptions(): Single<List<ServiceSubscription>>
}
