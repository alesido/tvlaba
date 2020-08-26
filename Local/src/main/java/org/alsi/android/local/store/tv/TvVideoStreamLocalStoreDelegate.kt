package org.alsi.android.local.store.tv

import android.content.Context
import android.text.format.DateUtils
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.datatv.store.TvVideoStreamLocalStore
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.local.mapper.tv.TvVideoStreamEntityMapper
import org.alsi.android.local.model.tv.TvVideoStreamEntity
import org.alsi.android.local.model.tv.TvVideoStreamEntity_
import java.net.URI
import javax.inject.Inject

class TvVideoStreamLocalStoreDelegate(
        serviceBoxStore: BoxStore
): TvVideoStreamLocalStore {

    @Inject
    lateinit var context: Context

    private val streamBox: Box<TvVideoStreamEntity> = serviceBoxStore.boxFor()

    private val mapper = TvVideoStreamEntityMapper()

    override fun getVideoStreamUri(channel: TvChannel, accessCode: String?) = Single.fromCallable {
        val now = System.currentTimeMillis()
        val result = streamBox.query {
            equal(TvVideoStreamEntity_.channelId, channel.id)
            equal(TvVideoStreamEntity_.programId, 0L)
            if (accessCode != null) equal(TvVideoStreamEntity_.accessCode, accessCode)
            less(TvVideoStreamEntity_.start, now + 1L)
            greater(TvVideoStreamEntity_.end, now - 1L)
        }.findFirst()
        result?.streamUri
    }

    override fun getVideoStreamUri(program: TvProgramIssue, accessCode: String?) = Single.fromCallable {
        streamBox.query{
            equal(TvVideoStreamEntity_.channelId, program.channelId)
            equal(TvVideoStreamEntity_.programId, program.programId?: 0L)
            if (accessCode != null) equal(TvVideoStreamEntity_.accessCode, accessCode)
            greater(TvVideoStreamEntity_.timeStamp, System.currentTimeMillis() - EXPIRATION_ARCHIVE_STREAM)
        }.findFirst()?.streamUri
    }

    override fun putVideoStreamUri(channel: TvChannel, streamUri: URI, accessCode: String?)
            = Completable.fromRunnable { streamBox.put(mapper.from(channel, streamUri, accessCode)) }

    override fun putVideoStreamUri(program: TvProgramIssue, streamUri: URI, accessCode: String?)
            = Completable.fromRunnable { streamBox.put(mapper.from(program, streamUri, accessCode))}

    companion object {
        const val EXPIRATION_ARCHIVE_STREAM = DateUtils.MINUTE_IN_MILLIS * 60
    }
}