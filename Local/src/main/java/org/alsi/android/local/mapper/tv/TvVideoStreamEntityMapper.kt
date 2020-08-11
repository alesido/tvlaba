package org.alsi.android.local.mapper.tv

import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.local.model.tv.TvVideoStreamEntity
import java.net.URI

class TvVideoStreamEntityMapper {

    fun from(channel: TvChannel, streamUri: URI, accessCode: String?) = with(channel) { TvVideoStreamEntity(
            channelId =  id,
            programId = 0L,
            streamUri = streamUri,
            timeStamp = System.currentTimeMillis(),
            accessCode = accessCode,
            start = live.time?.startUnixTimeMillis?: 0L,
            end = live.time?.endUnixTimeMillis?: 0L,
            title = title + " " + live.title
    )}

    fun from(program: TvProgramIssue, streamUri: URI, accessCode: String?) = with(program) { TvVideoStreamEntity(
            channelId =  program.channelId,
            programId = program.programId?: 0L,
            streamUri = streamUri,
            timeStamp = System.currentTimeMillis(),
            accessCode = accessCode,
            start = time?.startUnixTimeMillis?: 0L,
            end = time?.endUnixTimeMillis?: 0L,
            title = title
    )}
}