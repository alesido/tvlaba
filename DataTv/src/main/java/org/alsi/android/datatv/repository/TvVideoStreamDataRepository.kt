package org.alsi.android.datatv.repository

import io.reactivex.Single
import org.alsi.android.datatv.store.TvVideoStreamLocalStore
import org.alsi.android.datatv.store.TvVideoStreamRemoteStore
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.repository.guide.TvVideoStreamRepository
import java.net.URI
import javax.inject.Inject

open class TvVideoStreamDataRepository @Inject constructor(

        protected val local: TvVideoStreamLocalStore,
        protected val remote: TvVideoStreamRemoteStore

): TvVideoStreamRepository() {

    override fun getVideoStreamUri(channel: TvChannel, accessCode: String?): Single<URI?> {
        // try to get from local store first (the store may expire the URI),
        // get it from the remote if it is N/A locally and then store locally
        return local.getVideoStreamUri(channel, accessCode).onErrorResumeNext {
            //if (error !is NullPointerException) Single.just(error)
            // TODO Collect and report stream URI cache errors
            remote.getVideoStreamUri(channel, accessCode).flatMap {
                local.putVideoStreamUri(channel, it, accessCode)
                Single.just(it)
            }
        }
    }

    override fun getVideoStreamUri(program: TvProgramIssue, accessCode: String?): Single<URI?> {
        // try to get from local store first (the store may expire the URI),
        // get it from the remote if it is N/A locally and then store locally
        return local.getVideoStreamUri(program, accessCode).onErrorResumeNext {
            //if (error !is NullPointerException) Single.just(error)
            remote.getVideoStreamUri(program, accessCode).flatMap {
                local.putVideoStreamUri(program, it, accessCode)
                Single.just(it)
            }
        }
    }
}