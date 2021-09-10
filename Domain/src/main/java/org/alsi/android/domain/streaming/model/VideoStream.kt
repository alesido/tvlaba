package org.alsi.android.domain.streaming.model

import java.net.URI

class VideoStream (
        val uri: URI?,
        val kind: VideoStreamKind = VideoStreamKind.UNKNOWN,
        val subtitles: URI? = null,
        val timeStamp: Long? = null
) {
    fun isEmpty() = null == uri

    companion object {
        fun empty() = VideoStream(null)
    }
}

enum class VideoStreamKind {
    UNKNOWN, LIVE, RECORD
}
