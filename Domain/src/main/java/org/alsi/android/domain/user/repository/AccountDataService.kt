package org.alsi.android.domain.user.repository

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.UserAccount

interface AccountDataService
{
    fun login(loginName: String, loginPassword: String): Single<UserAccount>
    fun resume(loginName: String, skipRemoteLogin: Boolean = false): Single<UserAccount>

    fun getAccount(): Single<UserAccount>
    fun getLoginName(): Single<String>
    fun getPassword(): Single<String>

    fun getSubscriptions(): Single<List<ServiceSubscription>>

    fun setRememberMeAtLogin(value: Boolean): Completable
}
