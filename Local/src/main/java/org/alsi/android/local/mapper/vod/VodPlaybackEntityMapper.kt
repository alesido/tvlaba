package org.alsi.android.local.mapper.vod

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.local.model.vod.VodPlaybackEntity

class VodPlaybackEntityMapper: EntityMapper<VodPlaybackEntity, VodPlayback> {

    override fun mapFromEntity(entity: VodPlaybackEntity): VodPlayback = with(entity) {
        VodPlayback(
            sectionId, unitId, itemId, seriesId, title, description, season, series,
            VideoStream(streamUri, streamKind, subtitlesUri)
        )
    }

    override fun mapToEntity(domain: VodPlayback): VodPlaybackEntity = with(domain) {
        VodPlaybackEntity(
            0L, sectionId, unitId, itemId, seriesId, title, description, season, series,
            stream?.uri, stream?.kind?: VideoStreamKind.RECORD, stream?.subtitles
        )
    }
}