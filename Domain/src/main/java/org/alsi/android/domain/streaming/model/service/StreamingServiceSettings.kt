package org.alsi.android.domain.streaming.model.service

import org.alsi.android.domain.streaming.model.options.*
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlMap
import java.util.*

/**
 * .
 */
class StreamingServiceSettings (

        /** Subset of features supported by given streaming service.
         */
        val features: EnumSet<StreamingServiceFeature>? = null,



        // --- Streaming Server Options

        /** Streaming server setting = selected server option.
         */
        val server: StreamingServerOption? = null,

        /** Selected stream bitrate.
         */
        val bitrate: StreamBitrateOption? = null,

        /** Size of http cache for streaming. Used to adjust player buffering parameters.
         */
        val cacheSize: Long? = null,



        /** API Server selected by index in a list of default and fallback servers.
         */
        val api: ApiServerOption? = null,



        /** App language setting = selected language option
         */
        val language: LanguageOption? = null,



        // -- Remote Control Options

        /** Device model setting = selected device model option
         */
        val device: DeviceModelOption? = null,

        /** Remote control key assignments specific to the selected device model.
         */
        val rc: RemoteControlMap? = null,



        /** Time shift to watch "current" programs
         */
        val timeShiftSettingHours: Int? = null,
)
