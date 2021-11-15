package org.alsi.android.local.mapper

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramTimeInterval
import org.alsi.android.local.model.tv.TvPlaybackEntity

class TvPlaybackEntityMapper: EntityMapper<TvPlaybackEntity, TvPlayback> {

    override fun mapFromEntity(entity: TvPlaybackEntity): TvPlayback {
        return with(entity) {
            TvPlayback(
                channelId = channelId,
                // program ID isn't set for live playback of a channels w/o EPG, however it
                // is stored as 0 so it can be found with a search request
                programId = if (programId != 0L) programId else  null,
                stream = streamUri?.let { VideoStream(streamUri, streamKind, subtitlesUri) },
                time = if (start != null && end != null)
                    TvProgramTimeInterval(start!!, end!!) else null,
                title = title,
                description = description,
                isLive = isLive,
                isUnderParentControl = isUnderParentControl
            )
        }
    }

    override fun mapToEntity(domain: TvPlayback): TvPlaybackEntity {
        return with(domain) {
            TvPlaybackEntity(
                    channelId = channelId,
                    // program ID isn't set for live playback of a channels w/o EPG, however it
                    // is intentionally stored as 0 so it can be found with a search request
                    programId = programId?: 0L,
                    streamUri = stream?.uri,
                    streamKind = stream?.kind?:VideoStreamKind.UNKNOWN,
                    subtitlesUri = stream?.subtitles,
                    start = time?.startUnixTimeMillis,
                    end = time?.endUnixTimeMillis,
                    title = title,
                    description = description,
                    isLive = isLive,
                    isUnderParentControl = isUnderParentControl
            )
        }
    }

}