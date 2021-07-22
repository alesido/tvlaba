package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.session.TvPlayCursor
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

class TvRestorePlaybackUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<TvPlayback, TvRestorePlaybackUseCase.Params?>(postExecutionThread)
{

    override fun buildUseCaseObservable(params: Params?): Single<TvPlayback> {

        if (null == params) return Single.error(Throwable("@TvRestorePlaybackUseCase No parameters to get playback data!"))

        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (session !is TvSessionRepository)
            return Single.error(Throwable("@TvRestorePlaybackUseCase The TV Session Repository is N/A!"))

        return session.play.setCursorTo(params.storedPlaybackCursor)
    }

    class Params (val storedPlaybackCursor: TvPlayCursor)
}
