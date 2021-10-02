package org.alsi.android.local.mapper.vod

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.vod.model.session.VodPlayCursor
import org.alsi.android.local.model.vod.VodPlayCursorEntity

class VodPlayCursorEntityMapper: EntityMapper<VodPlayCursorEntity, VodPlayCursor> {

    private val playbackMapper = VodPlaybackEntityMapper()

    override fun mapFromEntity(entity: VodPlayCursorEntity) = with(entity) {
        VodPlayCursor(playbackMapper.mapFromEntity(playback.target), seekTime, timeStamp)
    }

    override fun mapToEntity(domain: VodPlayCursor) = with(domain) {
        val entity = VodPlayCursorEntity(id = 0L, seekTime = seekTime, timeStamp = timeStamp)
        entity.playback.target = playbackMapper.mapToEntity(playback)
        entity
    }
}