package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Completable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

class TvUpdatePlaybackCursorUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : CompletableUseCase<TvUpdatePlaybackCursorUseCase.Params?>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {

        if (null == params) return Completable.error(Throwable("No parameters to get playback data!"))

        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (session !is TvSessionRepository)
            return Completable.error(Throwable("The TV Directory Repository is N/A!"))

        return session.play.updateCursor(params.seekTime) // update playback cursor asynchronously
    }

    class Params constructor (val seekTime: Long)
}
