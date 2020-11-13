package org.alsi.android.local.mapper.tv

import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.local.model.tv.TvVideoStreamEntity
import java.net.URI

class TvVideoStreamEntityMapper {

    fun from(channel: TvChannel, stream: VideoStream, accessCode: String?) = with(channel) { TvVideoStreamEntity(
            channelId =  id,
            programId = 0L,
            streamUri = stream.uri,
            streamKind = stream.kind,
            subtitlesUri = stream.subtitles,
            timeStamp = System.currentTimeMillis(),
            accessCode = accessCode,
            start = live.time?.startUnixTimeMillis?: 0L,
            end = live.time?.endUnixTimeMillis?: 0L,
            title = title + " " + live.title
    )}

    fun from(program: TvProgramIssue, stream: VideoStream, accessCode: String?) = with(program) { TvVideoStreamEntity(
            channelId =  program.channelId,
            programId = program.programId?: 0L,
            streamUri = stream.uri,
            streamKind = stream.kind,
            subtitlesUri = stream.subtitles,
            timeStamp = System.currentTimeMillis(),
            accessCode = accessCode,
            start = time?.startUnixTimeMillis?: 0L,
            end = time?.endUnixTimeMillis?: 0L,
            title = title
    )}
}