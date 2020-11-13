package org.alsi.android.moidom.store.tv

import org.alsi.android.datatv.store.TvVideoStreamRemoteStore
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvVideoStreamRemoteStoreMoidom @Inject constructor(

        private val remoteService: RestServiceMoidom,
        private val remoteSession: RemoteSessionRepositoryMoidom

): TvVideoStreamRemoteStore {

    override fun getVideoStream(channel: TvChannel, accessCode: String?) = remoteSession.getSessionId()
            .flatMap { sid -> remoteService.getLiveVideoStreamUrl(
                    sid,
                    channelId = channel.id.toString(),
                    streamMode = RestServiceMoidom.QUERY_PARAM_STREAM_MODE_HLS,
                    accessCode = accessCode)
            }.map { response -> VideoStream(URI.create(response.url), VideoStreamKind.LIVE) }

    override fun getVideoStream(program: TvProgramIssue, accessCode: String?) = remoteSession.getSessionId()
            .flatMap { sid -> remoteService.getArchiveVideoStreamUrl(
                    sid,
                    channelId = program.channelId.toString(),
                    unixTimeStamp = TimeUnit.MILLISECONDS.toSeconds(program.time?.startUnixTimeMillis?:0L),
                    accessCode = accessCode)
            }.map { response ->
                VideoStream(URI.create(response.url), VideoStreamKind.RECORD)
                //URI.create("http://d3rlna7iyyu8wu.cloudfront.net/skip_armstrong/skip_armstrong_multi_language_subs.m3u8")
                //URI.create("https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8")
            }
}