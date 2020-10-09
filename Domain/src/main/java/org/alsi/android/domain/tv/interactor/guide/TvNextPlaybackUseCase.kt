package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.interactor.guide.TvNextPlayback.NEXT_CHANNEL
import org.alsi.android.domain.tv.interactor.guide.TvNextPlayback.PREVIOUS_CHANNEL
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvPlaybackMapper
import org.alsi.android.domain.tv.model.session.TvPlayCursor
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

class TvNextPlaybackUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<TvPlayback, TvNextPlaybackUseCase.Params?>(postExecutionThread)
{
    private val mapper = TvPlaybackMapper()

    override fun buildUseCaseObservable(params: Params?): Single<TvPlayback> {

        if (null == params) return Single.error(TvRepositoryError(
                TvRepositoryErrorKind.ERROR_WRONG_USE_CASE_PARAMETERS))

        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (directory !is TvDirectoryRepository || session !is TvSessionRepository)
            return Single.error(TvRepositoryError(TvRepositoryErrorKind.ERROR_WRONG_USE_CASE_PARAMETERS))

        return if (params.target in listOf(NEXT_CHANNEL, PREVIOUS_CHANNEL))
            navigateChannel(directory, session, params.target)
        else
            navigateProgram(directory, session, params.target)
    }

    /**
     *  Navigate next/previous channel in a category
     */
    private fun navigateChannel(directory: TvDirectoryRepository, session: TvSessionRepository,
                        target: TvNextPlayback): Single<TvPlayback> {

        return Single.zip(session.play.last(), directory.channels.getDirectory().firstOrError(),
                BiFunction<TvPlayCursor?, TvChannelDirectory, Pair<TvPlayCursor, TvChannelDirectory>> {
                    cursor, channelDirectory -> Pair(cursor, channelDirectory)
                }

        ).flatMap {
            val (cursor, channelDirectory) = it
            val categoryChannels = channelDirectory.index[cursor.categoryId]
                    ?: return@flatMap Single.error<TvPlayback>(TvRepositoryError(
                            TvRepositoryErrorKind.RESPONSE_NO_CHANNELS_IN_CATEGORY))
            val channelPosition = categoryChannels.indexOfFirst{ channel ->
                cursor.playback.channelId == channel.id
            }
            if (target == NEXT_CHANNEL && channelPosition == categoryChannels.lastIndex)
                return@flatMap Single.error<TvPlayback>(TvRepositoryError(
                        TvRepositoryErrorKind.RESPONSE_NO_NEXT_CHANNEL))
            if (target == PREVIOUS_CHANNEL && channelPosition == 0)
                return@flatMap Single.error<TvPlayback>(TvRepositoryError(
                        TvRepositoryErrorKind.RESPONSE_NO_PREVIOUS_CHANNEL))
            val targetChannel = categoryChannels[
                    if (target == NEXT_CHANNEL) channelPosition + 1 else channelPosition - 1]
            directory.streams.getVideoStreamUri(targetChannel, null, null).map {
                streamURI -> mapper.from(targetChannel, streamURI)
            }.flatMap { targetPlayback ->
                session.play.setCursorTo(cursor.categoryId, targetPlayback)
            }
        }
    }

    /**
     *  Navigate next/previous program in a schedule
     */
    private fun navigateProgram(directory: TvDirectoryRepository, session: TvSessionRepository,
                                target: TvNextPlayback): Single<TvPlayback> {
        return Single.error<TvPlayback>(Throwable(""))
    }

    /**
     *  Use Case Parameters
     */
    class Params constructor (val target: TvNextPlayback)
}

enum class TvNextPlayback {
    NEXT_CHANNEL, PREVIOUS_CHANNEL, NEXT_PROGRAM, PREVIOUS_PROGRAM
}