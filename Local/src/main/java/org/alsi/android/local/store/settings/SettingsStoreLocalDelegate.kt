package org.alsi.android.local.store.settings

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import org.alsi.android.data.repository.settings.SettingsDataLocal
import org.alsi.android.domain.streaming.model.options.*
import org.alsi.android.domain.streaming.model.options.DeviceModelOption
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlMap
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.streaming.model.service.StreamingServiceProfile
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.model.settings.*
import org.alsi.android.local.model.user.UserAccountEntity
import org.alsi.android.local.model.user.UserAccountEntity_

class SettingsStoreLocalDelegate(
        private val scopeTypeOrdinal: Int, // provider (1) or service (2)
        private val scopeId: Long, // provider ID or service ID depending on the scope type
        private val boxStore: BoxStore,
        private val defaults: StreamingServiceDefaults
)
    : SettingsDataLocal
{
    private val settingsBox by lazy<Box<ServiceSettingsEntity>> { boxStore.boxFor() }
    private val accountBox by lazy<Box<UserAccountEntity>> { boxStore.boxFor() }
    private val serverBox by lazy<Box<ServerOptionEntity>> { boxStore.boxFor() }
    private val languageBox by lazy<Box<LanguageOptionEntity>> { boxStore.boxFor() }
    private val deviceBox by lazy<Box<DeviceModelOptionEntity>> { boxStore.boxFor() }

    private var accountId: Long = 0L

    private var settingsQuery = settingsQuery()

    fun attach(domainAccount: UserAccount) {
        this.accountId = accountBox.query{ equal(UserAccountEntity_.loginName, domainAccount.loginName) }.findFirst()?.id?:0L
        this.settingsQuery = settingsQuery()
    }

    /**
     * prepare user settings query
     */
    private fun settingsQuery() = settingsBox.query {
        equal(ServiceSettingsEntity_.scopeTypeOrdinal, scopeTypeOrdinal.toLong())
        equal(ServiceSettingsEntity_.scopeId, scopeId)
        equal(ServiceSettingsEntity_.accountId, accountId)
    }

    private fun settingsEntity(): ServiceSettingsEntity
        = settingsQuery.findUnique()?: ServiceSettingsEntity(0L, scopeTypeOrdinal, scopeId, accountId)

    /**
     * store selected server option
     */
    override fun setServer(serverTag: String): Completable {
        return Completable.fromRunnable {
            val settingsEntity = settingsEntity()
            settingsEntity.server.target = serverBox.query { equal(ServerOptionEntity_.reference, serverTag) }.findFirst()
            settingsBox.put(settingsEntity)
        }
    }

    /**
     * store selected language option
     */
    override fun setLanguage(languageCode: String): Completable {
        return Completable.fromRunnable {
            val settingsEntity = settingsEntity()
            settingsEntity.language.target = languageBox.query {
                equal(LanguageOptionEntity_.code, languageCode) }.findUnique()
                    ?: LanguageOptionEntity(0L, defaults.getDefaultLanguageCode(), defaults.getDefaultLanguageName())
            settingsBox.put(settingsEntity)
        }
    }

    /**
     * store selected device option
     */
    override fun setDevice(modelId: String): Completable {
        return Completable.fromRunnable {
            val settingsEntity = settingsEntity()
            settingsEntity.device.target = deviceBox.query { equal(DeviceModelOptionEntity_.modelId, modelId) }.findUnique()
            settingsBox.put(settingsEntity)
        }
    }

    /**
     * export current settings from store
     */
    override fun values(): StreamingServiceSettings {
        val entity = settingsEntity()
        val server = entity.server.target
        val language  = entity.language.target
        val device = entity.device.target
        val rc = RemoteControlMap()
        device.remoteControlKeys.forEach { rc.put( it.function.reference, it.keyCode)}
        return StreamingServiceSettings(
                server = StreamingServerOption(server.reference, server.title, server.description),
                language = LanguageOption(language.code, language.name),
                device = DeviceModelOption(device.id, device.modelId),
                timeShiftSettingHours = 0,
                rc = rc)
    }

    /**
     * export all supported settings from store
     */
    override fun profile(): StreamingServiceProfile {
        return StreamingServiceProfile(
                servers = serverBox.all.map { StreamingServerOption(it.reference, it.title, it.description) },
                languages = languageBox.all.map { LanguageOption(it.code, it.name) },
                devices = deviceBox.all.map { DeviceModelOption(it.id, it.modelId) }
        )
    }

    /**
     * save settings to store
     *
     * NOTE Server, language and partially device settings are retrieved from profile records
     */
    override fun setValues(settings: StreamingServiceSettings) {
        val settingsEntity = settingsEntity()
        settings.server?.tag?.let {
            settingsEntity.server.target = serverBox.query { equal(ServerOptionEntity_.reference, it) }.findFirst()
        }
        settings.language?.code?.let {
            settingsEntity.language.target = languageBox.query {
                equal(LanguageOptionEntity_.code, it) }.findUnique()
                    ?: LanguageOptionEntity(0L, defaults.getDefaultLanguageCode(), defaults.getDefaultLanguageName())

        }
        settings.device?.name?.let {
            val deviceEntity = deviceBox.query { equal(DeviceModelOptionEntity_.modelId, it) }.findUnique()
                    ?: DeviceModelOptionEntity(0L, it)
            val valueByReference = RcFunctionProperty.valueByReference
            settings.rc?.remoteControlKeyCodeMap?.forEach { entry ->
                deviceEntity.remoteControlKeys.add(RemoteControlKeyEntity(
                        id = 0L,
                        function = valueByReference[entry.value]?:RcFunctionProperty.UNKNOWN,
                        keyCode = entry.key))
            }
            deviceBox.put(deviceEntity)
            settingsEntity.device.target = deviceEntity
        }
        settingsBox.put(settingsEntity)
    }

    /**
     * save all supported settings to store
     */
    override fun setProfile(profile: StreamingServiceProfile) {
        val servers = profile.servers.map { ServerOptionEntity(0L, it.tag, it.title, it.description) }
        serverBox.removeAll()
        serverBox.put(servers)
        val languages = profile.languages.map { LanguageOptionEntity(0L, it.code, it.name) }
        languageBox.removeAll()
        languageBox.put(languages)
        val devices = profile.devices.map{ DeviceModelOptionEntity(0L, it.name) }
        deviceBox.removeAll()
        deviceBox.put(devices)
    }
}