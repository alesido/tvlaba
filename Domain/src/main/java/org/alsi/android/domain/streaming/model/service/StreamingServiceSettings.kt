package org.alsi.android.domain.streaming.model.service

import org.alsi.android.domain.streaming.model.options.ApiServerOption
import org.alsi.android.domain.streaming.model.options.DeviceModelOption
import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.domain.streaming.model.options.StreamingServerOption
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlMap
import java.util.*

/**
 * .
 */
class StreamingServiceSettings (

        /** Subset of features supported by given streaming service.
         */
        val features: EnumSet<StreamingServiceFeature>?,



        // --- Streaming Server Options

        /** Streaming server setting = selected server option.
         */
        val server: StreamingServerOption?,

        /** Selected stream bitrate.
         */
        val bitrate: Int?,

        /** Size of http cache for streaming. Used to adjust player buffering parameters.
         */
        val cacheSize: Long?,



        /** API Server selected by index in a list of default and fallback servers.
         */
        val api: ApiServerOption?,



        /** App language setting = selected language option
         */
        val language: LanguageOption?,



        // -- Remote Control Options

        /** Device model setting = selected device model option
         */
        val device: DeviceModelOption?,

        /** Remote control key assignments specific to the selected device model.
         */
        val rc: RemoteControlMap?,



        /** Time shift to watch "current" programs
         */
        val timeShiftSettingHours: Int?,
)
