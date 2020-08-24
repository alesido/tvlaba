package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Observable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.ObservableUseCase
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

open class TvBrowseCursorObserveUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        executionThread: PostExecutionThread)
    : ObservableUseCase<TvBrowseCursor, Nothing?>(executionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Observable<TvBrowseCursor> {
        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (session !is TvSessionRepository)
            return Observable.error(Throwable("TV Service Repository is N/A!"))
        return session.browse.observeCursor()
    }
}
