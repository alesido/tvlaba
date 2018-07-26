package org.alsi.android.data.account

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.user.model.UserAccount

/** Contract on remote data store for service user/subscriber account.
 *
 */
interface AccountDataRemote
{
    fun login(loginName: String, loginPassword: String): Single<UserAccount>

    fun changeParentCode(currentCode: String, newCode: String): Completable
    fun setLanguage(languageCode: String): Completable
    fun setTimeShiftSettingHours(timeShiftHours: Int): Completable
}