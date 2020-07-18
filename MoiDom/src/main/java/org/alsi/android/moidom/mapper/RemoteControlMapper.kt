package org.alsi.android.moidom.mapper

import android.util.Log
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlFunction
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlMap
import org.alsi.android.moidom.model.LoginResponse
import org.alsi.android.remote.mapper.SourceDataMapper

class RemoteControlMapper: SourceDataMapper<List<LoginResponse.Settings.RcCode>, RemoteControlMap> {
    override fun mapFromSource(source: List<LoginResponse.Settings.RcCode>): RemoteControlMap {
        val result = RemoteControlMap()
        source.forEach{
            val key = when(it.func) {
                "aspect_ratio" -> RemoteControlFunction.ASPECT_RATIO
                "track_select" -> RemoteControlFunction.TRACK_SELECT
                "rewind_left" -> RemoteControlFunction.REWIND_LEFT
                "rewind_right" -> RemoteControlFunction.REWIND_RIGHT
                "rewind_left_5" -> RemoteControlFunction.REWIND_LEFT_FASTER
                "rewind_right_5" -> RemoteControlFunction.REWIND_RIGHT_FASTER
                "prog_minus" -> RemoteControlFunction.PREVIOUS_PROGRAM
                "prog_plus" -> RemoteControlFunction.NEXT_PROGRAM
                "prev_day" -> RemoteControlFunction.PREVIOUS_DAY
                "next_day" -> RemoteControlFunction.NEXT_DAY
                "tv_mode" -> RemoteControlFunction.TV_MODE
                "vod_mode" -> RemoteControlFunction.VOD_MODE
                "fav_add" -> RemoteControlFunction.ADD_FAVORITE_CHANNEL
                "fav_del" -> RemoteControlFunction.DELETE_FAVORITE_CHANNEL
                "fav_toggle" -> RemoteControlFunction.TOGGLE_FAVORITE_CHANNEL
                "settings" -> RemoteControlFunction.SETTINGS
                else -> RemoteControlFunction.UNKNOWN
            }
            if (key != RemoteControlFunction.UNKNOWN)
                result.put(key, it.code)
            else
                Log.w(RemoteControlMapper::class.simpleName, "Unknown RC functional key \"${it.func}\"")
        }
        return result
    }
}