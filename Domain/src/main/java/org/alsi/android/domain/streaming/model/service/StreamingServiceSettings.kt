package org.alsi.android.domain.streaming.model.service

import org.alsi.android.domain.streaming.model.options.DeviceModelOption
import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.domain.streaming.model.options.StreamingServerOption
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlMap

/**
 * .
 */
class StreamingServiceSettings (

        /** Streaming server setting = selected server option
         */
        val server: StreamingServerOption?,

        /** App language setting = selected language option
         */
        val language: LanguageOption?,

        /** Time shift to watch "current" programs
         */
        val timeShiftSettingHours: Int?,

        /** Device model setting = selected device model option
         */
        val device: DeviceModelOption?,

        /** Remote control key assignments specific to the selected device model.
         */
        val rc: RemoteControlMap?
)
