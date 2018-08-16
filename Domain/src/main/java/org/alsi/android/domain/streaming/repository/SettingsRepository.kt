package org.alsi.android.domain.streaming.repository

import io.reactivex.Completable
import org.alsi.android.domain.streaming.model.options.StreamingServiceProfile
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings

interface SettingsRepository
{
    fun selectServer(serverTag: String): Completable
    fun selectLanguage(languageCode: String): Completable
    fun selectDevice(modelId: String): Completable

    fun values(): StreamingServiceSettings
    fun profile(): StreamingServiceProfile
}