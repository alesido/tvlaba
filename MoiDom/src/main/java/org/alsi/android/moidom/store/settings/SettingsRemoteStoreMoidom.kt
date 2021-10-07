package org.alsi.android.moidom.store.settings

import io.reactivex.Completable
import org.alsi.android.data.repository.settings.SettingsDataRemote
import org.alsi.android.domain.streaming.model.options.DeviceModelOption
import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.domain.streaming.model.options.StreamingServerOption
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.streaming.model.service.StreamingServiceProfile
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.moidom.mapper.RemoteControlMapper
import org.alsi.android.moidom.model.LoginResponse
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import javax.inject.Inject

class SettingsRemoteStoreMoidom @Inject constructor (
    private val remoteService: RestServiceMoidom,
    private val remoteSession: RemoteSessionRepositoryMoidom,
    private val defaults: StreamingServiceDefaults

): SettingsDataRemote {

    fun getSourceSettings(source: LoginResponse, profile: StreamingServiceProfile): StreamingServiceSettings {

        val settings = source.settings

        val serverTag = profile.serverByTag[settings.stream_server?.value]
        val serverSetting = serverTag?.let { StreamingServerOption(serverTag.tag, serverTag.title, serverTag.description )}

        val languageCode = settings.language?.value
        val languageName = languageCode?.let { profile.languageNameByCode[languageCode] }
        val languageSetting = if (languageName != null) LanguageOption(languageCode, languageName)
        else LanguageOption(defaults.getDefaultLanguageCode(), defaults.getDefaultLanguageName())

        val deviceModelId = settings.device_model?.value?.toLong()
        val deviceModelName = deviceModelId?.let { profile.deviceById[deviceModelId] }
        val deviceModelSetting = deviceModelName?.let { DeviceModelOption(deviceModelId, deviceModelName) }

        val rcMap = RemoteControlMapper().mapFromSource(source.settings.rc_codes)

        return StreamingServiceSettings(serverSetting, languageSetting, settings.timeshift.value, deviceModelSetting, rcMap)
    }

    fun getSourceProfile(source: LoginResponse): StreamingServiceProfile {

        val serverOptions: MutableList<StreamingServerOption> = mutableListOf()
        source.settings.stream_server?.list?.forEach { serverOptions.add(StreamingServerOption(it.ip, it.ip, it.descr)) }

        val languageOptions: MutableList<LanguageOption> = mutableListOf()
        source.settings.language?.list?.forEach { languageOptions.add(LanguageOption(it.id, it.name)) }

        val deviceModelOptions: MutableList<DeviceModelOption> = mutableListOf()
        source.settings.device_model?.list?.forEach { deviceModelOptions.add(DeviceModelOption(it.id.toLong(), it.name)) }

        return StreamingServiceProfile(serverOptions, languageOptions, deviceModelOptions)
    }

    override fun selectServer(serverTag: String): Completable {
        return remoteSession.getSessionId().flatMapCompletable { sessionId ->
            remoteService.setSetting(sessionId,
                    RestServiceMoidom.QUERY_PARAM_SETTING_NAME_STREAM_SERVER,
                    serverTag).ignoreElement()
        }
    }

    override fun selectLanguage(languageCode: String): Completable {
        return remoteSession.getSessionId().flatMapCompletable { sessionId ->
            remoteService.setSetting(sessionId,
                    RestServiceMoidom.QUERY_PARAM_SETTING_NAME_LANGUAGE,
                    languageCode).ignoreElement()
        }
    }

    override fun selectDevice(modelName: String): Completable {
        return remoteSession.getSessionId().flatMapCompletable { sessionId ->
            remoteService.setSetting(sessionId,
                    RestServiceMoidom.QUERY_PARAM_SETTING_DEVICE_NAME_MODEL,
                    modelName).ignoreElement()
        }
    }
}