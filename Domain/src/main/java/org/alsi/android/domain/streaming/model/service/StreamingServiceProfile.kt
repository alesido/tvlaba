package org.alsi.android.domain.streaming.model.service

import org.alsi.android.domain.streaming.model.options.*

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
/**
 *  Profile of streaming service. Set of options for settings/preferences.
 */
class StreamingServiceProfile(

    val servers: List<StreamingServerOption>,
    val bitrates: List<StreamBitrateOption>?,
    val cacheSizes: List<Long>?,

    val api: List<ApiServerOption>?,

    val languages: List<LanguageOption>,

    val devices: List<DeviceModelOption>)
{
    val serverByTag = servers.map { it.tag to it}.toMap()
    val languageNameByCode = languages.map { it.code to it.name }.toMap()
    val deviceById = devices.map { it.id to it.name }.toMap()
}
