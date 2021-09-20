package org.alsi.android.domain.vod.interactor

import io.reactivex.Observable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.ObservableUseCase
import org.alsi.android.domain.vod.model.session.VodBrowseCursor
import org.alsi.android.domain.vod.model.session.VodSessionRepository
import javax.inject.Inject

open class VodBrowseCursorObserveUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        executionThread: PostExecutionThread)
    : ObservableUseCase<VodBrowseCursor, Nothing?>(executionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Observable<VodBrowseCursor> {
        val session = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)?.session
        if (session !is VodSessionRepository)
            return Observable.error(Throwable("TV Service Repository is N/A!"))
        return session.browse.observeCursor()
    }
}
