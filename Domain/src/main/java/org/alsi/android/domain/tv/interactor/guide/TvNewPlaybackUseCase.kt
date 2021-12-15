package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

class TvNewPlaybackUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<TvPlayback, TvNewPlaybackUseCase.Params?>(postExecutionThread)
{
    private val mapper = TvPlaybackMapper()

    private lateinit var foundChannel: TvChannel

    override fun buildUseCaseObservable(params: Params?): Single<TvPlayback> {

        if (null == params) return Single.error(Throwable("No parameters to get playback data!"))

        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (directory !is TvDirectoryRepository || session !is TvSessionRepository)
            return Single.error(Throwable("The TV Directory Repository is N/A!"))

        with(params) {

            // live stream case
            program ?: let {
                channel ?: return Single.error(
                    Throwable("Wrong parameters for LIVE stream playback!"))

                // update channel live data first
                return directory.programs.getChannelLive(channel.id).flatMap {
                    channel.live = TvProgramLive(it.time, it.title, it.description)
                    directory.streams.getVideoStream(channel, session.parentalControlPassword)
                }.map {
                    stream -> mapper.from(channel, stream)
                }.flatMap {
                    playback -> session.play.setCursorTo(categoryId, playback)
                }
            }

            // archive stream case
            return if (null == channel) {
                directory.channels.findChannelById(program.channelId).flatMap {
                    foundChannel = it
                    directory.streams.getVideoStream(foundChannel, program,
                        session.parentalControlPassword)
                }
                    .map { stream -> mapper.from(foundChannel, program, stream) }
                    .flatMap { playback -> session.play.setCursorTo(categoryId, playback) }

            } else {
                directory.streams.getVideoStream(channel, program, session.parentalControlPassword)
                    .map { stream -> mapper.from(channel, program, stream) }
                    .flatMap { playback -> session.play.setCursorTo(categoryId, playback) }
            }
        }
    }

    class Params constructor (val categoryId: Long, val channel: TvChannel? = null, val program: TvProgramIssue? = null)
}
