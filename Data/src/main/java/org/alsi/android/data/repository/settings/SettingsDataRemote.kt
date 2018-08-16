package org.alsi.android.data.repository.settings

import io.reactivex.Completable

interface SettingsDataRemote {

    fun selectServer(serverTag: String): Completable
    fun selectLanguage(languageCode: String): Completable
    fun selectDevice(modelName: String): Completable
}