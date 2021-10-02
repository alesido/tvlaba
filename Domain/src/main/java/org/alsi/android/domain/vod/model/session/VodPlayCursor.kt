package org.alsi.android.domain.vod.model.session

import org.alsi.android.domain.vod.model.guide.playback.VodPlayback

data class VodPlayCursor(
    var playback: VodPlayback,
    var seekTime: Long,
    var timeStamp: Long
) {
    fun isEmpty() = playback.isEmpty()

    companion object {
        fun empty() = VodPlayCursor(VodPlayback.empty(), -1L, -1L)
    }
}