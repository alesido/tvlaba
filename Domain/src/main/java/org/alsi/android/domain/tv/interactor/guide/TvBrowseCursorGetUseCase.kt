package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

open class TvBrowseCursorGetUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        executionThread: PostExecutionThread)
    : SingleObservableUseCase<TvBrowseCursor, Nothing?>(executionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Single<TvBrowseCursor> {
        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (session !is TvSessionRepository)
            return Single.error(Throwable("TV Service Repository is N/A!"))
        return session.browse.getCursor()
    }
}
