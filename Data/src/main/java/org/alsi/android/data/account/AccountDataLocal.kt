package org.alsi.android.data.account

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.user.model.ServiceSubscription

/** Local synchronized copy of a user account of a streaming service user.
 */
interface AccountDataLocal
{
    fun getLoginName(): Single<String>
    fun getPassword(): Single<String>

    fun getParentCode(): Single<String>
    fun getLanguage(): Single<String>
    fun getTimeShiftSettingHours(): Single<Int>

    fun getSubscriptions(): Single<List<ServiceSubscription>>

    fun setParentCode(code: String): Completable
    fun setLanguage(currentCode: String): Completable
    fun setTimeShiftSettingHours(hours: Int): Completable

    fun setSubscriptions(subscription: List<ServiceSubscription>): Completable
}