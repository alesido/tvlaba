package org.alsi.android.domain.tv.model.guide

import java.net.URI

class TvPlaybackMapper {

    fun from(channel: TvChannel, streamUri: URI) : TvPlayback {
        with (channel.live) {
            return TvPlayback(
                    channelId = channel.id,
                    programId = time?.startUnixTimeMillis?:0L,
                    streamUri = streamUri,
                    time = time,
                    title = title,
                    description = description
            )
        }
    }

    fun from(program: TvProgramIssue, streamUri: URI) : TvPlayback {
        with (program) {
            return TvPlayback (
                    channelId = channelId,
                    programId = programId,
                    streamUri = streamUri,
                    time = time,
                    title = title,
                    description = description
            )
        }
    }
}