package org.alsi.android.domain.vod.model.guide.playback

import org.alsi.android.domain.streaming.model.VideoStream

/**
 *  VOD playback record is to continue playback on restart, to maintain playback history,
 *  to coordinate fragmentvia playback cursor.
 */
class VodPlayback (

    // Identity

    val sectionId: Long,
    val unitId: Long,
    val itemId: Long,
    val seriesId: Long? = null,

    // Presentation

    val title: String? = null,
    val description: String? = null,
    val season: Int? = null,
    val series: Int? = null,

    // Video

    var stream: VideoStream?,
) {
    var position: Long = 0L

    fun isEmpty() = sectionId == -1L

    companion object {
        fun empty() = VodPlayback( -1L, -1L, -1L, -1L, stream = null)
    }
}