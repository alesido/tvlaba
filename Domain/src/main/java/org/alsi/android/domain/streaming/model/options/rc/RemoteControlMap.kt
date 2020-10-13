package org.alsi.android.domain.streaming.model.options.rc

import java.util.HashMap

class RemoteControlMap {

    val remoteControlKeyCodeMap = HashMap<Int, RemoteControlFunction>()

    fun put(key: RemoteControlFunction, code: Int) {
        remoteControlKeyCodeMap[code] = key
    }

    fun mapCodeToKey(keyCode: Int): RemoteControlFunction? {
        return try {
            remoteControlKeyCodeMap[keyCode]
        } catch (x: Exception) {
            null
        }
    }
}
