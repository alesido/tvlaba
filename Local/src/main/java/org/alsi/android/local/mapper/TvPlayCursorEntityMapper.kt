package org.alsi.android.local.mapper

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.tv.model.session.TvPlayCursor
import org.alsi.android.local.model.tv.TvPlayCursorEntity

class TvPlayCursorEntityMapper: EntityMapper<TvPlayCursorEntity, TvPlayCursor> {

    private val playbackMapper = TvPlaybackEntityMapper()

    override fun mapFromEntity(entity: TvPlayCursorEntity): TvPlayCursor {
        return with(entity) {
            TvPlayCursor(categoryId,
                    playbackMapper.mapFromEntity(playback.target),
                    timeStamp, seekTime)
        }
    }

    override fun mapToEntity(domain: TvPlayCursor): TvPlayCursorEntity {
        return with (domain) {
            val entity = TvPlayCursorEntity(id = 0L, categoryId = categoryId,
                    seekTime = seekTime, timeStamp = timeStamp)
            entity.playback.target = playbackMapper.mapToEntity(domain.playback)
            entity
        }
    }
}