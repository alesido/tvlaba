package org.alsi.android.moidom.store.account

import android.os.Build
import android.text.TextUtils
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.data.repository.account.AccountDataRemote
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.mapper.AccountEntityMapper
import org.alsi.android.moidom.BuildConfig
import org.alsi.android.moidom.mapper.LoginResponseMapper
import org.alsi.android.moidom.store.RestServiceMoidom
import java.net.NetworkInterface
import javax.inject.Inject

/**
 * Created on 7/26/18.
 */
class AccountServiceRemoteMoidom @Inject constructor(
        private val remoteService: RestServiceMoidom,
        private val loginResponseMapper: LoginResponseMapper)
    : AccountDataRemote {

    private val accountMapper = AccountEntityMapper()

    override fun login(loginName: String, loginPassword: String): Single<UserAccount> {
        return remoteService.login(
                loginName,
                loginPassword,
                settings = RestServiceMoidom.QUERY_PARAM_LOGIN_SETTINGS_DEFAULT,
                deviceTypeAndroid = RestServiceMoidom.QUERY_PARAM_DEVICE_TYPE,
                appBuildNumber = BuildConfig.VERSION_CODE,
                androidSdkNumber = Build.VERSION.SDK_INT,
                deviceSerialNumber = getDeviceSerialNumber(),
                macAddress = getMacAddressToIdentifyDevice("UNKNOWN"),
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

    private fun getDeviceSerialNumber(): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.USER
        }
        catch (x: SecurityException) {
            TODO("Add request for READ_PHONE_STATE permission to send serial number of the device.")
        }
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
                .first { it.name.toLowerCase() != networkInterfaceName.toLowerCase() }
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