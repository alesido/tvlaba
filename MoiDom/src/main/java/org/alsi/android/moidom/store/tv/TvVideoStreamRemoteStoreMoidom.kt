package org.alsi.android.moidom.store.tv

import org.alsi.android.datatv.store.TvVideoStreamRemoteStore
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TvVideoStreamRemoteStoreMoidom @Inject constructor(

        private val remoteService: RestServiceMoidom,
        private val remoteSession: RemoteSessionRepositoryMoidom

): TvVideoStreamRemoteStore {

    override fun getVideoStreamUri(channel: TvChannel, accessCode: String?) = remoteSession.getSessionId()
            .flatMap { sid -> remoteService.getLiveVideoStreamUrl(
                    sid,
                    channelId = channel.id.toString(),
                    streamMode = RestServiceMoidom.QUERY_PARAM_STREAM_MODE_HLS,
                    accessCode = accessCode)
            }.map { response -> URI.create(response.url) }

    override fun getVideoStreamUri(program: TvProgramIssue, accessCode: String?) = remoteSession.getSessionId()
            .flatMap { sid -> remoteService.getArchiveVideoStreamUrl(
                    sid,
                    channelId = program.channelId.toString(),
                    unixTimeStamp = TimeUnit.MILLISECONDS.toSeconds(program.time?.startUnixTimeMillis?:0L),
                    accessCode = accessCode)
            }.map { response -> URI.create(response.url) }
}