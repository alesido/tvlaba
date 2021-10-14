package org.alsi.android.domain.streaming.repository

import io.reactivex.Completable
import io.reactivex.Observable
import org.alsi.android.domain.streaming.model.service.StreamingServiceProfile
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings

interface SettingsRepository
{
    fun selectServer(serverTag: String): Completable
    fun selectCacheSize(cacheSize: Long): Completable
    fun selectStreamBitrate(bitrate: Int): Completable
    fun selectLanguage(languageCode: String): Completable
    fun selectDevice(modelId: String): Completable

    fun values(): Observable<StreamingServiceSettings>
    fun profile(): Observable<StreamingServiceProfile>
}