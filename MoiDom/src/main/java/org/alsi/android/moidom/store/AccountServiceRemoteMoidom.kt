package org.alsi.android.moidom.store

import android.os.Build
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.data.account.AccountDataRemote
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.moidom.BuildConfig
import org.alsi.android.moidom.RestServiceMoiDom
import org.alsi.android.moidom.mapper.AccountMapperMoidom
import org.alsi.android.moidom.mapper.SubscriptionMapperMoidom
import org.alsi.android.moidom.model.remote.LoginResponse
import javax.inject.Inject

/**
 * Created on 7/26/18.
 */
class AccountServiceRemoteMoidom @Inject constructor(
        private val remoteService: RestServiceMoiDom,
        private val internalStore: InternalStoreMoidom
): AccountDataRemote {

    private val accountMapper = AccountMapperMoidom()

    override fun login(loginName: String, loginPassword: String): Single<UserAccount> {
        return remoteService.login(
                loginName,
                loginPassword,
                settings = RestServiceMoiDom.QUERY_PARAM_LOGIN_SETTINGS_DEFAULT,
                deviceTypeAndroid = RestServiceMoiDom.QUERY_PARAM_DEVICE_TYPE,
                appBuildNumber = BuildConfig.VERSION_CODE,
                androidSdkNumber = Build.VERSION.SDK_INT,
                deviceSerialNumber = "UNKNOWN",
                macAddress = "mac", //Utils.getNonEmptyMacAddress(),
                deviceModel = "model",// getDeviceModelName(),
                manufacturer = Build.MANUFACTURER)
                .flatMapCompletable { data -> internalStore.importLoginResponseData(data, loginName, loginPassword) }
                .andThen(internalStore.getAccount())
                .flatMap { accountEntity -> Single.just(accountMapper.mapFromEntity(accountEntity)) }
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