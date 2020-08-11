package org.alsi.android.local.mapper

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramTimeInterval
import org.alsi.android.local.model.tv.TvPlaybackEntity

class TvPlaybackEntityMapper: EntityMapper<TvPlaybackEntity, TvPlayback> {

    override fun mapFromEntity(entity: TvPlaybackEntity): TvPlayback {
        return with(entity) {
            TvPlayback(
                    channelId = channelId,
                    programId = programId,
                    streamUri = streamUri,
                    time = TvProgramTimeInterval(start, end),
                    title = title,
                    description = description
            )
        }
    }

    override fun mapToEntity(domain: TvPlayback): TvPlaybackEntity {
        return with(domain) {
            TvPlaybackEntity(
                    channelId = channelId,
                    programId = programId?: 0L,
                    streamUri = streamUri,
                    start = time?.startUnixTimeMillis?: 0L,
                    end = time?.endUnixTimeMillis?: 0L,
                    title = title,
                    description = description
            )
        }
    }

}