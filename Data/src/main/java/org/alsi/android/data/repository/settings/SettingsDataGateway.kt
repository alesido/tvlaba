package org.alsi.android.data.repository.settings

import io.reactivex.Completable
import org.alsi.android.domain.streaming.model.service.StreamingServiceProfile
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.domain.streaming.repository.SettingsRepository

/**
 * Created on 8/11/18.
 */
open class SettingsDataGateway(
        val remote: SettingsDataRemote,
        val local: SettingsDataLocal)
    : SettingsRepository {

    override fun selectServer(serverTag: String): Completable
            = remote.selectServer(serverTag).andThen(local.setServer(serverTag))

    override fun selectLanguage(languageCode: String): Completable
            = remote.selectLanguage(languageCode).andThen(local.setLanguage(languageCode))

    override fun selectDevice(modelId: String): Completable
            = remote.selectDevice(modelId).andThen(local.setDevice(modelId))

    override fun values(): StreamingServiceSettings = local.values()

    override fun profile(): StreamingServiceProfile = local.profile()
}