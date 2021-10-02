package org.alsi.android.domain.vod.model.guide.playback

import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem

class VodPlaybackMapper {

    fun from(vod: VodListingItem, stream: VideoStream, seriesId: Long? = null): VodPlayback = with(vod) {
        val series: VodListingItem.Video.Series? =
            if (seriesId != null && video is VodListingItem.Video.Serial) {
                video.series.find { series -> series.id == seriesId }
            } else null

        VodPlayback(
            sectionId?:-1L, unitId?:-1L, id, seriesId,
            series?.title?: title,  series?.description?: description,
            series?.season, series?.episode, stream
        )
    }
}