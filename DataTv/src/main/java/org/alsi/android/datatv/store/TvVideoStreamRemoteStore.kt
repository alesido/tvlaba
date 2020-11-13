package org.alsi.android.datatv.store

import io.reactivex.Single
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import java.net.URI

interface TvVideoStreamRemoteStore {

    fun getVideoStream(channel: TvChannel, accessCode: String?): Single<VideoStream>

    fun getVideoStream(program: TvProgramIssue, accessCode: String?): Single<VideoStream>
}