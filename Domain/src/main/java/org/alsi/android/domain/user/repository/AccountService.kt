package org.alsi.android.domain.user.repository

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.UserAccount

interface AccountService
{
    fun login(loginName: String, loginPassword: String): Single<UserAccount>

    fun changeParentCode(currentCode: String, newCode: String): Completable
    fun setLanguage(languageCode: String): Completable
    fun setTimeShiftSettingHours(timeShiftHours: Int): Completable

    fun getLoginName(): Single<String>
    fun getPassword(): Single<String>

    fun getParentCode(): Single<String>
    fun getLanguage(): Single<String>
    fun getTimeShiftSettingHours(): Single<Int>

    fun getSubscriptions(): Single<List<ServiceSubscription>>
}
