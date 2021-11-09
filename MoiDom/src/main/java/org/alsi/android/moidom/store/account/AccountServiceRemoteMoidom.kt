package org.alsi.android.moidom.store.account

import android.os.Build
import android.text.TextUtils
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.alsi.android.data.repository.account.AccountDataRemote
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceRegistry
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.mapper.AccountSourceDataMapperMoiDom
import org.alsi.android.moidom.model.LoginEvent
import org.alsi.android.moidom.store.RestServiceMoidom
import java.net.NetworkInterface
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created on 7/26/18.
 */
@Singleton
class AccountServiceRemoteMoidom @Inject constructor(

    @Named("${Moidom.TAG}.${StreamingService.TV}")
    private val defaultTvServiceId: Long,
    @Named("${Moidom.TAG}.${StreamingService.VOD}")
    private val defaultVodServiceId: Long,

    private val remoteService: RestServiceMoidom,
    private val loginSubject: PublishSubject<LoginEvent>,
    private val serviceRegistry: StreamingServiceRegistry
)
    : AccountDataRemote
{
    var lastLoginEvent: LoginEvent? = null

    override fun login(loginName: String, loginPassword: String): Single<UserAccount> {
        return remoteService.login(
                loginName,
                loginPassword,
                settings = RestServiceMoidom.QUERY_PARAM_LOGIN_SETTINGS_DEFAULT,
                deviceTypeAndroid = RestServiceMoidom.QUERY_PARAM_DEVICE_TYPE,
                appBuildNumber = 1, // TODO Fetch VERSION_CODE into the library module, see "https://developer.android.com/studio/releases/gradle-plugin?hl=ru"
                androidSdkNumber = Build.VERSION.SDK_INT,
                deviceSerialNumber = getDeviceSerialNumber(),
                macAddress = getMacAddressToIdentifyDevice("UNKNOWN"),
                deviceModel = "model",// TODO getDeviceModelName(),
                manufacturer = Build.MANUFACTURER)
                .map { loginResponse ->
                    val account = AccountSourceDataMapperMoiDom(
                        defaultTvServiceId, defaultVodServiceId,
                        loginName, loginPassword,
                        serviceRegistry
                    ).mapFromSource(loginResponse)
                    //loginSubject.onNext(LoginEvent(account, loginResponse))
                    lastLoginEvent = LoginEvent(account, loginResponse)
                    account
                }
    }

    override fun notifyOnLogin() {
        lastLoginEvent?: return
        loginSubject.onNext(lastLoginEvent!!)
        lastLoginEvent = null
    }

    override fun onLoginResume(account: UserAccount): Single<UserAccount> {
        loginSubject.onNext(LoginEvent(account, null))
        return Single.just(account)
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

    private fun getDeviceSerialNumber(): String {
        return Build.USER
//        return try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.USER
//        }
//        catch (x: SecurityException) {
//            Build.USER
//        }
    }

    private fun getMacAddressToIdentifyDevice(defaultMacAddress: String): String {
        getMacAddress("eth0").let { ethernetMacAddress ->
            if (! TextUtils.isEmpty(ethernetMacAddress)) return ethernetMacAddress?: defaultMacAddress
        }
        getMacAddress("wlan0").let { wifiMacAddress ->
            if (! TextUtils.isEmpty(wifiMacAddress)) return wifiMacAddress?: defaultMacAddress
        }
        return defaultMacAddress
    }

    private fun getMacAddress(networkInterfaceName: String): String? {
        try {
            NetworkInterface.getNetworkInterfaces().toList()
                .first { it.name.toLowerCase(Locale.US) != networkInterfaceName.toLowerCase(Locale.US) }
                .let {
                    val macBytes = it.hardwareAddress ?: return null
                    val builder = StringBuilder()
                    for (b in macBytes) {
                        builder.append(Integer.toHexString(b.toInt() and 0xFF)).append(':')
                    }
                    if (builder.isNotEmpty()) {
                        builder.deleteCharAt(builder.length - 1)
                    }
                    return builder.toString()
                }
        }
        catch (x: Exception) {
            return null
        }
    }
}