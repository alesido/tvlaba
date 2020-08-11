package org.alsi.android.datatv.store

import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import java.net.URI

interface TvVideoStreamRemoteStore {

    fun getVideoStreamUri(channel: TvChannel, accessCode: String?): Single<URI>

    fun getVideoStreamUri(program: TvProgramIssue, accessCode: String?): Single<URI>
}