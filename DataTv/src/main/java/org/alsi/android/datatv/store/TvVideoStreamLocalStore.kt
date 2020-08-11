package org.alsi.android.datatv.store

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import java.net.URI

/**
 * NOTE Implementation should take into account a stream URI expiration. A live URI is expired as
 * soon as it ends and as it known from the channel data on its current live. An archive stream
 * URI may, by convention, expire since some time amount from the moment it was cached.
 *
 */
interface TvVideoStreamLocalStore {

    fun getVideoStreamUri(channel: TvChannel, accessCode: String?): Single<URI?>
    fun putVideoStreamUri(channel: TvChannel, streamUri: URI, accessCode: String?): Completable

    fun getVideoStreamUri(program: TvProgramIssue, accessCode: String?): Single<URI?>
    fun putVideoStreamUri(program: TvProgramIssue, streamUri: URI, accessCode: String?): Completable
}