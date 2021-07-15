package org.alsi.android.domain.context.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.context.model.SessionActivityType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.ServiceProvider
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

/**
 *  Setup user session i.e.:
 *
 *  - require user to do login, if nobody logged in yet, or it was too long ago,
 *  - or make auto-login, if the last login record is quite old,
 *  - or even skip login, if the last login record is fresh enough.
 */
open class  TryResumeSessionUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    private val provider: ServiceProvider,
    executionThread: PostExecutionThread)
    : SingleObservableUseCase<SessionActivityType, Nothing?>(executionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Single<SessionActivityType> {

        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (session !is TvSessionRepository)
            return Single.error(Throwable("The TV Directory Repository is N/A!"))

        // TODO Add check for the latest browsing time
        return session.play.latest().flatMap {
            if (it.isEmpty()) {
                return@flatMap Single.just(SessionActivityType.LOGIN)
            }
            provider.accountService.resume(it.loginName, true).map { SessionActivityType.PLAYBACK_TV }
        }
    }
}