package org.alsi.android.domain.vod.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.domain.vod.model.session.VodPlayCursor
import org.alsi.android.domain.vod.model.session.VodSessionRepository
import javax.inject.Inject

class VodRestorePlaybackUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<VodPlayback, VodRestorePlaybackUseCase.Params?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Params?): Single<VodPlayback> {

        params?: return Single.error(Throwable("@TvRestorePlaybackUseCase No parameters to get playback data!"))

        val context = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)
            ?: return Single.error(Throwable("VOD context is N/A!"))

        val session = context.session as VodSessionRepository

        return with(params.storedPlaybackCursor) {
            session.play.setCursorTo(playback, seekTime)
        }

    }

    class Params (val storedPlaybackCursor: VodPlayCursor)
}
