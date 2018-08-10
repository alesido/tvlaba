package org.alsi.android.data.repository.account

import io.reactivex.Completable
import org.alsi.android.domain.user.repository.AccountDataService

/**
 *  This is a gateway between higher level account data service/repository and
 *  concrete service(s)  for authentication, authorization and user
 *  account data operations.
 */
open class AccountDataGateway(
        private val remote: AccountDataRemote,
        private val local: AccountDataLocal): AccountDataService
{
    override fun login(loginName: String, loginPassword: String) = remote.login(loginName, loginPassword)

    override fun changeParentCode(currentCode: String, newCode: String) : Completable
            = remote.changeParentCode(currentCode, newCode).andThen { local.setParentCode(currentCode) }

    override fun setLanguage(languageCode: String) : Completable
            = remote.setLanguage(languageCode).andThen { local.setLanguage(languageCode) }

    override fun setTimeShiftSettingHours(timeShiftHours: Int): Completable
            = remote.setTimeShiftSettingHours(timeShiftHours).andThen { local.setTimeShiftSettingHours(timeShiftHours) }

    override fun getLoginName() = local.getLoginName()
    override fun getPassword() = local.getPassword()

    override fun getParentCode() = local.getParentCode()
    override fun getLanguage() = local.getLanguage()
    override fun getTimeShiftSettingHours() = local.getTimeShiftSettingHours()
    override fun getSubscriptions() = local.getSubscriptions()
}