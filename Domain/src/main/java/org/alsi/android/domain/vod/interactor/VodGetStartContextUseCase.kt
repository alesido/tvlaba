package org.alsi.android.domain.vod.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.vod.model.guide.VodStartContext
import org.alsi.android.domain.vod.model.session.VodSessionRepository
import javax.inject.Inject

/**
 *  Get initial (restored) browsing and playback context of current user.
 *
 *  Assumed that "current user" is "attached" to local stores, i.e. selected
 *  local stores belonging to the user (configured to work with data of the user).
 */
class VodGetStartContextUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    executionThread: PostExecutionThread
): SingleObservableUseCase<VodStartContext, Nothing?>(executionThread) {

    override fun buildUseCaseObservable(params: Nothing?): Single<VodStartContext> {

        val service = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)
        val session = service?.session
        if (session !is VodSessionRepository)
            return Single.error(Throwable("The VOD Directory Repository for ${service!!.tag} is N/A!"))

        return Single.zip(session.browse.getStoredCursorReference(), session.play.last(),
            session.mostRecentActivity(service.id)) {
            browseCursorReference, playCursor, recentActivity ->
            VodStartContext(browseCursorReference, playCursor, recentActivity)
        }
    }
}