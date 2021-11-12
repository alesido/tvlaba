package org.alsi.android.data.repository.settings

import io.reactivex.Completable

interface SettingsDataRemote {
    fun selectServer(serverTag: String): Completable
    fun selectBitrate(bitrate: Int): Completable
    fun selectCacheSize(cacheSize: Long): Completable
    fun switchApiServer(): Completable
    fun selectLanguage(languageCode: String): Completable
    fun selectDevice(modelName: String): Completable
    fun changeParentalControlPass(currentPin: String, newPin: String): Completable
}