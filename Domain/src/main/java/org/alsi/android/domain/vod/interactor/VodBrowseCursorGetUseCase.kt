package org.alsi.android.domain.vod.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.vod.model.session.VodBrowseCursor
import org.alsi.android.domain.vod.model.session.VodSessionRepository
import javax.inject.Inject

open class VodBrowseCursorGetUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        executionThread: PostExecutionThread)
    : SingleObservableUseCase<VodBrowseCursor, Nothing?>(executionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Single<VodBrowseCursor> {
        val session = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)?.session
        if (session !is VodSessionRepository)
            return Single.error(Throwable("VOD Service Repository is N/A!"))
        return session.browse.getCursor()
    }
}
