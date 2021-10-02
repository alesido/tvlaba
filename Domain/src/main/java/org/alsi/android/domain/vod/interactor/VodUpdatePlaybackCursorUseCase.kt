package org.alsi.android.domain.vod.interactor

import io.reactivex.Completable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.domain.vod.model.session.VodSessionRepository
import javax.inject.Inject

class VodUpdatePlaybackCursorUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : CompletableUseCase<VodUpdatePlaybackCursorUseCase.Params?>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {

        if (null == params) return Completable.error(Throwable("No parameters to get playback data!"))

        val session = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)?.session
        if (session !is VodSessionRepository)
            return Completable.error(Throwable("The TV Directory Repository is N/A!"))

        return session.play.updateCursor(params.currentPlayback) // update playback cursor asynchronously
    }

    class Params(val currentPlayback: VodPlayback)
}
