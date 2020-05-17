package org.alsi.android.local.store.settings

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import org.alsi.android.data.repository.settings.SettingsDataLocal
import org.alsi.android.domain.streaming.model.options.*
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.Local
import org.alsi.android.local.model.settings.*
import org.alsi.android.local.model.user.UserAccountEntity
import org.alsi.android.local.model.user.UserAccountEntity_
import javax.inject.Inject
import javax.inject.Named

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

    private fun settingsQuery() = settingsBox.query {
        equal(ServiceSettingsEntity_.scopeTypeOrdinal, scopeTypeOrdinal.toLong())
        equal(ServiceSettingsEntity_.scopeId, scopeId)
        equal(ServiceSettingsEntity_.accountId, accountId)
    }

    private fun settingsEntity(): ServiceSettingsEntity
        = settingsQuery.findUnique()?: ServiceSettingsEntity(0L, scopeTypeOrdinal, scopeId, accountId)

    override fun setServer(serverTag: String): Completable {
        return Completable.fromRunnable {
            val settingsEntity = settingsEntity()
            settingsEntity.server.target = serverBox.query { equal(ServerOptionEntity_.reference, serverTag) }.findFirst()
            settingsBox.put(settingsEntity)
        }
    }

    override fun setLanguage(languageCode: String): Completable {
        return Completable.fromRunnable {
            val settingsEntity = settingsEntity()
            settingsEntity.language.target = languageBox.query {
                equal(LanguageOptionEntity_.code, languageCode) }.findUnique()
                    ?: LanguageOptionEntity(0L, defaults.getDefaultLanguageCode(), defaults.getDefaultLanguageName())
            settingsBox.put(settingsEntity)
        }
    }

    override fun setDevice(modelId: String): Completable {
        return Completable.fromRunnable {
            val settingsEntity = settingsEntity()
            settingsEntity.device.target = deviceBox.query { equal(DeviceModelOptionEntity_.modelId, modelId) }.findUnique()
            settingsBox.put(settingsEntity)
        }
    }

    override fun values(): StreamingServiceSettings {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun profile(): StreamingServiceProfile {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setValues(settings: StreamingServiceSettings) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setProfile(profile: StreamingServiceProfile) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}