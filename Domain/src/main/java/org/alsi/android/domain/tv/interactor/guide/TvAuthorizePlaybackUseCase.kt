package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvPlaybackMapper
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

/** Request stream again using parental password available
 */
class TvAuthorizePlaybackUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<TvPlayback, TvAuthorizePlaybackUseCase.Params?>(postExecutionThread)
{
    private val mapper = TvPlaybackMapper()

    private var targetChannel: TvChannel? = null
    private var targetProgram: TvProgramIssue? = null

    override fun buildUseCaseObservable(params: Params?): Single<TvPlayback> {

        if (null == params) return Single.error(Throwable("No parameters to get playback data!"))

        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (directory !is TvDirectoryRepository || session !is TvSessionRepository)
            return Single.error(Throwable("The TV Directory Repository is N/A!"))

        val streamSingle = with (params.playback) {
            if (programId == -1000L) {
                // live playback
                directory.channels.findChannelById(channelId).flatMap { channel ->
                    targetChannel = channel
                    directory.streams
                        .getVideoStream(channel, null, session.parentalControlPassword)
                }
            }
            else {
                // archive playback
                directory.channels.findChannelById(channelId).map {
                    targetChannel = it
                }.flatMap {
                    directory.programs.getArchiveProgram(channelId,
                        time!!.startDateTime.toLocalDateTime())
                }.flatMap { program ->
                    targetProgram = program
                    directory.streams
                        .getVideoStream(null, program, session.parentalControlPassword)
                }
            }
        }

        return streamSingle.map { stream ->
            targetProgram?.let {
                mapper.from(targetChannel!!, targetProgram!!, stream)
            }?: let {
                mapper.from(targetChannel!!, stream)
            }
        }.flatMap { playback ->
            session.play.setCursorTo(targetChannel!!.categoryId, playback)
        }
    }

    class Params constructor (val playback: TvPlayback)
}
