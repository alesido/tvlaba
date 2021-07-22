package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.model.guide.TvStartContext
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

class TvGetStartContextUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    executionThread: PostExecutionThread
): SingleObservableUseCase<TvStartContext, Nothing?>(executionThread) {

    override fun buildUseCaseObservable(params: Nothing?): Single<TvStartContext> {

        val service = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)
        val session = service?.session
        if (session !is TvSessionRepository)
            return Single.error(Throwable("The TV Directory Repository is N/A!"))

        return Single.zip(session.browse.getStoredCursorReference(), session.play.last(),
            session.mostRecentActivity(service.id)) {
            browseCursorReference, playCursor, recentActivity ->
            TvStartContext(browseCursorReference, playCursor, recentActivity)
        }
    }
}