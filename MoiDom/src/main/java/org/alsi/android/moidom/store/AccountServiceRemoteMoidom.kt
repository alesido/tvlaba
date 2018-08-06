package org.alsi.android.moidom.store

import android.os.Build
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.data.repository.account.AccountDataRemote
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.moidom.BuildConfig
import org.alsi.android.local.mapper.AccountMapperMoidom
import org.alsi.android.moidom.mapper.LoginResponseMapper
import javax.inject.Inject

/**
 * Created on 7/26/18.
 */
class AccountServiceRemoteMoidom @Inject constructor(
        private val remoteService: RestServiceMoidom,
        private val loginResponseMapper: LoginResponseMapper)
    : AccountDataRemote {

    private val accountMapper = AccountMapperMoidom()

    override fun login(loginName: String, loginPassword: String): Single<UserAccount> {
        return remoteService.login(
                loginName,
                loginPassword,
                settings = RestServiceMoidom.QUERY_PARAM_LOGIN_SETTINGS_DEFAULT,
                deviceTypeAndroid = RestServiceMoidom.QUERY_PARAM_DEVICE_TYPE,
                appBuildNumber = BuildConfig.VERSION_CODE,
                androidSdkNumber = Build.VERSION.SDK_INT,
                deviceSerialNumber = "UNKNOWN",
                macAddress = "mac", //Utils.getNonEmptyMacAddress(),
                deviceModel = "model",// getDeviceModelName(),
                manufacturer = Build.MANUFACTURER)
                .map { data -> loginResponseMapper.importLoginResponseData(data, loginName, loginPassword) }
                .map { accountEntity -> accountMapper.mapFromEntity(accountEntity) }
    }

    override fun changeParentCode(currentCode: String, newCode: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLanguage(languageCode: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTimeShiftSettingHours(timeShiftHours: Int): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}