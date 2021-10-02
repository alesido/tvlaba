package org.alsi.android.domain.vod.interactor

import io.reactivex.Observable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.ObservableUseCase
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.domain.vod.model.session.VodSessionRepository
import javax.inject.Inject

open class VodCurrentPlaybackUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        executionThread: PostExecutionThread)
    : ObservableUseCase<VodPlayback, Nothing?>(executionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Observable<VodPlayback> {
        val session = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)?.session
        if (session !is VodSessionRepository)
            return Observable.error(Throwable("Vod Service Repository is N/A!"))
        return session.play.current()
    }
}
