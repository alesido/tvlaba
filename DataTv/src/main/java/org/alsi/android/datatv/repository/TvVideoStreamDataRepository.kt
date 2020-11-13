package org.alsi.android.datatv.repository

import io.reactivex.Single
import org.alsi.android.datatv.store.TvVideoStreamLocalStore
import org.alsi.android.datatv.store.TvVideoStreamRemoteStore
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.repository.guide.TvVideoStreamRepository
import javax.inject.Inject

open class TvVideoStreamDataRepository @Inject constructor(

        protected val local: TvVideoStreamLocalStore,
        protected val remote: TvVideoStreamRemoteStore

): TvVideoStreamRepository() {

    override fun getVideoStream(channel: TvChannel, accessCode: String?): Single<VideoStream?> {
        // try to get from local store first (the store may expire the URI),
        // get it from the remote if it is N/A locally and then store locally
        return local.getVideoStream(channel, accessCode).onErrorResumeNext {
            //if (error !is NullPointerException) Single.just(error)
            // TODO Collect and report stream URI cache errors
            remote.getVideoStream(channel, accessCode).flatMap {
                local.putVideoStream(channel, it, accessCode)
                Single.just(it)
            }
        }
    }

    override fun getVideoStream(program: TvProgramIssue, accessCode: String?): Single<VideoStream?> {
        // try to get from local store first (the store may expire the URI),
        // get it from the remote if it is N/A locally and then store locally
        return local.getVideoStream(program, accessCode).onErrorResumeNext {
            //if (error !is NullPointerException) Single.just(error)
            remote.getVideoStream(program, accessCode).flatMap {
                local.putVideoStream(program, it, accessCode)
                Single.just(it)
            }
        }
    }
}