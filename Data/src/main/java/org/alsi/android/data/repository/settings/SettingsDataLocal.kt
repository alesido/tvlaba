package org.alsi.android.data.repository.settings

import io.reactivex.Completable
import org.alsi.android.domain.streaming.model.service.StreamingServiceProfile
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings

interface SettingsDataLocal {

    fun setServer(serverTag: String): Completable
    fun setLanguage(languageCode: String): Completable
    fun setDevice(modelId: String): Completable

    fun values(): StreamingServiceSettings
    fun profile(): StreamingServiceProfile

    fun setValues(settings: StreamingServiceSettings)
    fun setProfile(profile: StreamingServiceProfile)
}