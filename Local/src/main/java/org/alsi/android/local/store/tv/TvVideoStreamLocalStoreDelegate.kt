package org.alsi.android.local.store.tv

import android.text.format.DateUtils
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.datatv.store.TvVideoStreamLocalStore
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.local.mapper.tv.TvVideoStreamEntityMapper
import org.alsi.android.local.model.tv.TvVideoStreamEntity
import org.alsi.android.local.model.tv.TvVideoStreamEntity_

class TvVideoStreamLocalStoreDelegate(
        serviceBoxStore: BoxStore
): TvVideoStreamLocalStore {

    private val streamBox: Box<TvVideoStreamEntity> = serviceBoxStore.boxFor()

    private val mapper = TvVideoStreamEntityMapper()

    override fun getVideoStream(channel: TvChannel, accessCode: String?) = Single.fromCallable {
        val now = System.currentTimeMillis()
        val result = streamBox.query {
            equal(TvVideoStreamEntity_.channelId, channel.id)
            equal(TvVideoStreamEntity_.programId, 0L)
            if (accessCode != null) equal(TvVideoStreamEntity_.accessCode, accessCode)
            less(TvVideoStreamEntity_.start, now + 1L)
            greater(TvVideoStreamEntity_.end, now - 1L)
            greater(TvVideoStreamEntity_.timeStamp, System.currentTimeMillis() - EXPIRATION_LIVE_STREAM)
        }.findFirst()
        result?.let {
            VideoStream(it.streamUri, it.streamKind, it.subtitlesUri)
        }
    }

    override fun getVideoStream(program: TvProgramIssue, accessCode: String?) = Single.fromCallable {
        streamBox.query{
            equal(TvVideoStreamEntity_.channelId, program.channelId)
            equal(TvVideoStreamEntity_.programId, program.programId?: 0L)
            if (accessCode != null) equal(TvVideoStreamEntity_.accessCode, accessCode)
            greater(TvVideoStreamEntity_.timeStamp, System.currentTimeMillis() - EXPIRATION_ARCHIVE_STREAM)
        }.findFirst()?.let {
            VideoStream(it.streamUri, it.streamKind, it.subtitlesUri)
        }
    }

    override fun putVideoStream(channel: TvChannel, stream: VideoStream, accessCode: String?)
            = Completable.fromRunnable { streamBox.put(mapper.from(channel, stream, accessCode)) }

    override fun putVideoStream(program: TvProgramIssue, stream: VideoStream, accessCode: String?)
            = Completable.fromRunnable { streamBox.put(mapper.from(program, stream, accessCode))}

    companion object {
        // TODO Make stream expiration time as a field of the entity so as it may be defined
        //  by a remote data source
        const val EXPIRATION_LIVE_STREAM = DateUtils.MINUTE_IN_MILLIS
        const val EXPIRATION_ARCHIVE_STREAM = DateUtils.MINUTE_IN_MILLIS
    }
}