package org.alsi.android.domain.streaming.model.service

import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.domain.streaming.model.options.StreamingServerOption
import org.alsi.android.domain.streaming.model.options.DeviceModelOption

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
/** Streaming service profile for user preferences.
 */
class StreamingServiceProfile(
        val servers: List<StreamingServerOption>,
        val languages: List<LanguageOption>,
        val devices: List<DeviceModelOption>) {

    val serverByTag = servers.map { it.tag to it}.toMap()
    val languageNameByCode = languages.map { it.code to it.name }.toMap()
    val deviceById = devices.map { it.id to it.name }.toMap()
}
