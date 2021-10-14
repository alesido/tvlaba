package org.alsi.android.moidom.store.settings

import io.reactivex.Completable
import org.alsi.android.data.repository.settings.SettingsDataRemote
import org.alsi.android.domain.exception.model.ApiException
import org.alsi.android.domain.exception.model.ExceptionMessages
import org.alsi.android.domain.exception.model.ApiSuspended
import org.alsi.android.domain.exception.model.RequestError
import org.alsi.android.domain.streaming.model.options.*
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.domain.streaming.model.service.StreamingServiceFeature
import org.alsi.android.domain.streaming.model.service.StreamingServiceProfile
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.moidom.mapper.RemoteControlMapper
import org.alsi.android.moidom.model.LoginResponse
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.QUERY_PARAM_SETTING_DEVICE_NAME_MODEL
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.QUERY_PARAM_SETTING_NAME_BITRATE
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.QUERY_PARAM_SETTING_NAME_HTTP_CACHING
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.QUERY_PARAM_SETTING_NAME_LANGUAGE
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.QUERY_PARAM_SETTING_NAME_STREAM_SERVER
import java.util.*
import javax.inject.Inject

class SettingsRemoteStoreMoidom @Inject constructor (
    private val remoteService: RestServiceMoidom,
    private val remoteSession: RemoteSessionRepositoryMoidom,
    private val defaults: StreamingServiceDefaults,
    private val messages: ExceptionMessages

): SettingsDataRemote {

    fun getSourceSettings(source: LoginResponse, profile: StreamingServiceProfile): StreamingServiceSettings {
        val settings = source.settings

        return StreamingServiceSettings(
            features = source.services.let {
                val set = EnumSet.noneOf(StreamingServiceFeature::class.java)
                if (it.containsKey("megogo")) set.add(StreamingServiceFeature.EXTRA_VOD)
                set
            },
            server = settings.stream_server?.value?.let { profile.serverByTag[it]?.copy() },
            bitrate = settings.bitrate?.let { b -> StreamBitrateOption(b.value,
                    (b.names.find { it.`val` == b.value })?.title ?: b.value.toString()) },
            cacheSize = settings.http_caching?.value?.toLong(),
            api = settings.api_hosts?.let { set ->
                set.fallback.find { it.url != null || it.url == set.default_url }?.let {
                    ApiServerOption( it.name, it.url!!)
                } ?: ApiServerOption(set.default_url, set.default_url)
            },
            language = settings.language?.let {
                 val name = profile.languageNameByCode[it.value]
                 if (name != null) LanguageOption(it.value, name)
                 else LanguageOption(defaults.getDefaultLanguageCode(),
                     defaults.getDefaultLanguageName())
            },
            device = settings.device_model?.value?.let { id ->
                val name = profile.deviceById[id.toLong()]
                name?.let { DeviceModelOption(id.toLong(), name) }
            },
            rc = RemoteControlMapper().mapFromSource(source.settings.rc_codes),
            timeShiftSettingHours = settings.timeshift.value,
        )
    }

    fun getSourceProfile(source: LoginResponse): StreamingServiceProfile {
        val settings = source.settings
        return StreamingServiceProfile(
            servers = settings.stream_server?.list?.map {
                StreamingServerOption(it.ip, it.ip, it.descr)
            }?: listOf(),
            bitrates = settings.bitrate?.names?.map { StreamBitrateOption(it.`val`, it.title) },
            cacheSizes = settings.http_caching?.list?.map { it.toLong() },
            api = settings.api_hosts?.let { set ->
                set.fallback.filter { it.url != null && it.url != set.default_url }
                    .distinctBy { it.url }
                    .map { ApiServerOption(it.name, it.url!!) }
            },
            languages = settings.language?.list?.map { LanguageOption(it.id, it.name) }?: listOf(),
            devices = settings.device_model?.list?.map {
                DeviceModelOption(it.id.toLong(), it.name)
            }?: listOf()
        )
    }

    private fun select(name: String, value: String)
    = remoteSession.getSessionId().flatMapCompletable {
        remoteService.setSetting(it, name, value).ignoreElement()
    }

    override fun selectServer(serverTag: String)
    = select(QUERY_PARAM_SETTING_NAME_STREAM_SERVER, serverTag)

    override fun selectBitrate(bitrate: Int)
      = select(QUERY_PARAM_SETTING_NAME_BITRATE, bitrate.toString())

    override fun selectCacheSize(cacheSize: Long)
      = select(QUERY_PARAM_SETTING_NAME_HTTP_CACHING, cacheSize.toString())

    override fun switchApiServer(): Completable {
        return Completable.complete() // TODO Switch to next API Server
    }

    override fun selectLanguage(languageCode: String)
      = select(QUERY_PARAM_SETTING_NAME_LANGUAGE, languageCode)

    override fun selectDevice(modelName: String): Completable
      = select(QUERY_PARAM_SETTING_DEVICE_NAME_MODEL, modelName).doOnError { t ->
        if (t is RequestError) {
            // This API exception have to have a better wording
            throw ApiSuspended(messages.settingTemporarilyNotAvailable(), t)
        }
    }
}