package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.interactor.guide.TvNextPlayback.*
import org.alsi.android.domain.tv.model.guide.*
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

        val service = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)
            ?: return Single.error(TvRepositoryError(
                    TvRepositoryErrorKind.ERROR_CANNOT_ACCESS_TV_REPOSITORY))

        with(service) {
            if (directory !is TvDirectoryRepository || session !is TvSessionRepository)
                return Single.error(TvRepositoryError(TvRepositoryErrorKind.ERROR_WRONG_USE_CASE_PARAMETERS))

            return if (params.target in listOf(NEXT_CHANNEL, PREVIOUS_CHANNEL))
                navigateChannel(directory, session, params.target)
            else
                navigateProgram(directory, session, params.target)
        }
    }

    /**
     *  Navigate next/previous channel in a category
     */
    private fun navigateChannel(directory: TvDirectoryRepository, session: TvSessionRepository,
                        target: TvNextPlayback): Single<TvPlayback> {

        return Single.zip( session.play.last(), directory.channels.observeDirectory().firstOrError(), {
                    cursor, channelDirectory -> Pair(cursor, channelDirectory)
                }

        ).flatMap {
            val (cursor, channelDirectory) = it
            // position of the channel at cursor
            val categoryChannels = channelDirectory.index[cursor.categoryId]
                    ?: return@flatMap Single.error<TvPlayback>(TvRepositoryError(
                            TvRepositoryErrorKind.RESPONSE_NO_CHANNELS_IN_CATEGORY))
            val channelPosition = categoryChannels.indexOfFirst{ channel ->
                cursor.playback.channelId == channel.id
            }
            // decline navigation outside category
            if (target == NEXT_CHANNEL && channelPosition == categoryChannels.lastIndex)
                return@flatMap Single.error<TvPlayback>(TvRepositoryError(
                        TvRepositoryErrorKind.RESPONSE_NO_NEXT_CHANNEL))
            if (target == PREVIOUS_CHANNEL && channelPosition == 0)
                return@flatMap Single.error<TvPlayback>(TvRepositoryError(
                        TvRepositoryErrorKind.RESPONSE_NO_PREVIOUS_CHANNEL))
            // retrieve target playback & set cursor to it
            val targetChannel = categoryChannels[
                    if (target == NEXT_CHANNEL) channelPosition + 1 else channelPosition - 1]
            directory.streams.getVideoStream(targetChannel, null, null).map {
                stream -> mapper.from(targetChannel, stream)
            }.flatMap { targetPlayback ->
                session.play.setCursorTo(cursor.categoryId, targetPlayback)
            }
        }
    }

    private fun channelAtCursor(cursor: TvPlayCursor, channelDirectory: TvChannelDirectory): TvChannel? {
        val categoryChannels = channelDirectory.index[cursor.categoryId]?: return null
        return categoryChannels.first{ channel ->
            cursor.playback.channelId == channel.id
        }
    }

    /**
     *  Navigate next/previous program in a schedule
     */
    private fun navigateProgram(
            directory: TvDirectoryRepository, session: TvSessionRepository, target: TvNextPlayback
    ): Single<TvPlayback> {

        return Single.zip( session.play.last(), directory.channels.observeDirectory().firstOrError(), {
                    cursor, channelDirectory -> Pair(cursor, channelDirectory)
                }
        ).flatMap {

            val (cursor, channelDirectory) = it

            // channel at cursor
            val channel = channelAtCursor(cursor, channelDirectory)
                    ?: return@flatMap Single.error<TvPlayback>(TvRepositoryError(
                    TvRepositoryErrorKind.RESPONSE_CANNOT_NAVIGATE_PROGRAM))

            // date of schedule at cursor
            val date = cursor.playback.time?.startDateTime?.toLocalDate()
                    ?: // played live on a channel w/o EPG - cannot navigate
                    return@flatMap Single.error<TvPlayback>(TvRepositoryError(
                            TvRepositoryErrorKind.RESPONSE_CANNOT_NAVIGATE_PROGRAM))

            // schedule at cursor
            return@flatMap directory.programs.getDaySchedule(
                    cursor.playback.channelId, date).flatMap<TvProgramIssue> schedule@ { schedule ->

                // target program position
                val programPosition = schedule.positionOf(cursor.playback)
                programPosition?: return@schedule Single.error<TvProgramIssue>(TvRepositoryError(
                        TvRepositoryErrorKind.RESPONSE_CANNOT_NAVIGATE_PROGRAM))
                val targetPosition = if (target == NEXT_PROGRAM) programPosition + 1 else programPosition - 1

                // target program playback
                if (targetPosition >= 0 && targetPosition < schedule.items.size) {
                    return@schedule Single.just(schedule.items[targetPosition])
                }
                else if (targetPosition < 0) {
                    // switch to the previous day, program before the current
                    return@schedule directory.programs.getDaySchedule(cursor.playback.channelId,
                            date.minusDays(1)).map { scheduleBefore ->
                        if (scheduleBefore.items.last().programId != cursor.playback.programId)
                            scheduleBefore.items.last()
                        else
                            scheduleBefore.items[scheduleBefore.items.size - 2]
                    }
                }
                else {
                    // switch to the next day, program after the current
                    return@schedule directory.programs.getDaySchedule(cursor.playback.channelId,
                            date.plusDays(1)).map { scheduleAfter ->
                        if (scheduleAfter.items.first().programId != cursor.playback.programId)
                            scheduleAfter.items.first()
                        else
                            scheduleAfter.items[1]
                    }
                }

            // get URI of the stream and compose playback data
            }.flatMap { targetProgram ->
                directory.streams.getVideoStream(channel, targetProgram, null).map {
                    videoStream -> mapper.from(channel, targetProgram, videoStream)
                }

            // set cursor to the playback
            }.flatMap { targetPlayback ->
                session.play.setCursorTo(cursor.categoryId, targetPlayback)
            }
        }
    }

    /**
     *  Use Case Parameters
     */
    class Params constructor (val target: TvNextPlayback)
}

enum class TvNextPlayback {
    NEXT_CHANNEL, PREVIOUS_CHANNEL, NEXT_PROGRAM, PREVIOUS_PROGRAM
}