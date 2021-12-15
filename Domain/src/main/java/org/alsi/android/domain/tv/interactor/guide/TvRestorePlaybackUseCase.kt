package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.domain.tv.model.session.TvPlayCursor
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

class TvRestorePlaybackUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<TvPlayback, TvRestorePlaybackUseCase.Params?>(postExecutionThread)
{
    private val mapper = TvPlaybackMapper()

    private lateinit var channel: TvChannel
    private lateinit var program: TvProgramIssue

    override fun buildUseCaseObservable(params: Params?): Single<TvPlayback> {

        params?: return Single.error(Throwable("@TvRestorePlaybackUseCase No parameters to get playback data!"))

        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (directory !is TvDirectoryRepository || session !is TvSessionRepository)
            return Single.error(Throwable("The TV Directory Repository is N/A!"))

        return if (params.storedPlaybackCursor.playback.stream?.kind == VideoStreamKind.LIVE)
            // stored playback was a LIVE one at the moment it recorded - restore it as the LIVE
            // playback of the channel it belongs accordingly to the business rule
            restoreLivePlayback(params.storedPlaybackCursor.playback, directory, session)
        else
            // stored stream was a RECORD, it was possibly a LIVE RECORD at the time, any way,
            // continue it as RECORD
            restoreRecordPlayback(params.storedPlaybackCursor, directory, session)
    }

    private fun restoreLivePlayback(
        storedPlayback: TvPlayback,
        directory: TvDirectoryRepository,
        session: TvSessionRepository): Single<TvPlayback> {

        return directory.channels.findChannelById(storedPlayback.channelId).flatMap {
            channel = it
            // get actual live program data as stored channel data may expire at the moment of restart
            directory.programs.getChannelLive(storedPlayback.channelId)
        }.flatMap {
            // set actual live program data to the channel object
            channel.live = TvProgramLive(it.time, it.title, it.description)
            directory.streams.getVideoStream(channel, null)
        }.map { stream ->
            mapper.from(channel, stream)
        }.flatMap {
           restoredPlayback -> session.play.setCursorTo(channel.categoryId, restoredPlayback)
        }
    }

    private fun restoreRecordPlayback(
        cursor: TvPlayCursor,
        directory: TvDirectoryRepository,
        session: TvSessionRepository): Single<TvPlayback> {

        return directory.channels.findChannelById(cursor.playback.channelId).flatMap {
            channel = it
            directory.programs.getArchiveProgram(cursor.playback.channelId,
                cursor.playback.time?.startDateTime?.toLocalDateTime()!!)
        }.flatMap {
            program = it
            directory.streams.getVideoStream(channel, program, null)
        }.map { stream ->
            mapper.from(channel, program, stream)
        }.flatMap { playback ->
            playback.position = cursor.seekTime
            session.play.setCursorTo(channel.categoryId, playback)
        }
    }

    class Params (val storedPlaybackCursor: TvPlayCursor)
}
