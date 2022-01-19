package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

/**
 *  Note, that this use case does not touch playback cursor as TvNewPlaybackUseCase does.
 *  It is just makes the stream URL is up to date, because it may expire quickly.
 */
class TvUpdateStreamUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<VideoStream, TvUpdateStreamUseCase.Params?>(postExecutionThread)
{
    private lateinit var channel: TvChannel
    private lateinit var program: TvProgramIssue

    override fun buildUseCaseObservable(params: Params?): Single<VideoStream> {
        if (null == params
            || null == params.playback.channelNumber
            || null == params.playback.time?.startDateTime)
            return Single.error(Throwable("No parameters to get playback data!"))

        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (directory !is TvDirectoryRepository || session !is TvSessionRepository)
            return Single.error(Throwable("The TV Directory Repository is N/A!"))

        return if (params.playback.isLive) {
            directory.channels.findChannelById(params.playback.channelId).flatMap {
                directory.streams.getVideoStream(it, session.parentalControlPassword)
            }
        } else {
            directory.channels.findChannelById(params.playback.channelId).flatMap {
                channel = it
                directory.programs.getArchiveProgram(params.playback.channelId,
                    params.playback.time.startDateTime.toLocalDateTime())
            }.flatMap {
                program = it
                directory.streams.getVideoStream(channel, program, session.parentalControlPassword)
            }
        }
    }

    class Params constructor (val playback: TvPlayback)
}