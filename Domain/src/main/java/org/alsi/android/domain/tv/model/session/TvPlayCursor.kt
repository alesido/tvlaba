package org.alsi.android.domain.tv.model.session

import org.alsi.android.domain.tv.model.guide.TvPlayback

class TvPlayCursor(
        var categoryId: Long,
        var playback: TvPlayback,
        var timeStamp: Long,
        var seekTime: Long
) {
        fun isEmpty() = categoryId == -1L

        companion object {
                fun empty() = TvPlayCursor(-1L, playback = TvPlayback.empty(), timeStamp = -1L, seekTime = -1L)
        }
}