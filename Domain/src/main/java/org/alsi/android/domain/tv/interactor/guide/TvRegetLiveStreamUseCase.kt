package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

/**
 *  Note, that this use case does not touch playback cursor as TvNewPlaybackUseCase does in order
 *  to support switching back from live record stream (current playback logically not changed)
 *  to correspondent live stream. And despite that live stream URL is usually available already
 *  at the moment it is still required because the live stream URL may expire quickly.
 */
class TvRegetLiveStreamUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<VideoStream, TvRegetLiveStreamUseCase.Params?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Params?): Single<VideoStream> {

        if (null == params || null == params.playback.channelNumber)
            return Single.error(Throwable("No parameters to get playback data!"))

        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (directory !is TvDirectoryRepository || session !is TvSessionRepository)
            return Single.error(Throwable("The TV Directory Repository is N/A!"))

        return directory.channels.findChannelById(params.playback.channelId).flatMap {
            directory.streams.getVideoStream(it, session.parentalControlPassword)
        }
    }

    class Params constructor (val playback: TvPlayback)
}
