package org.alsi.android.domain.context.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.context.model.SessionActivityType
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.ServiceProvider
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import org.joda.time.DateTimeUtils
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 *  Attempt to resume user session by skipping login or making auto-login if the
 *  last session is quite recent. Require user to login, if nobody logged in yet,
 *  or the last user activity was too long ago.
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

        return Single.zip(session.browse.mostRecent(), session.play.mostRecent(), { browse, play ->
            Pair(browse, play)
        }).flatMap {
            val (browse, play) = it
            if (browse.isEmpty() && play.isEmpty())
                return@flatMap Single.just(SessionActivityType.LOGIN)
            if (play.isEmpty())
                return@flatMap resumeActivity(browse)
            return@flatMap resumeActivity(
                if (browse.timeStamp > play.timeStamp) browse else play)
        }
    }

    private fun resumeActivity(activity: UserActivityRecord): Single<SessionActivityType> {
        val nowTimeStamp = DateTimeUtils.currentTimeMillis()
        return when {
            nowTimeStamp - activity.timeStamp < ELAPSED_TIME_LIMIT_FOR_AUTO_LOGIN -> {
                provider.accountService.resume(activity.loginName, true)
                    .map { SessionActivityType.PLAYBACK_TV }
            }
            nowTimeStamp - activity.timeStamp < ELAPSED_TIME_LIMIT_FOR_MANUAL_LOGIN -> {
                provider.accountService.resume(activity.loginName, false)
                    .map { SessionActivityType.PLAYBACK_TV }
            }
            else -> {
                Single.just(SessionActivityType.LOGIN)
            }
        }
    }

    companion object {
        val ELAPSED_TIME_LIMIT_FOR_MANUAL_LOGIN = TimeUnit.DAYS.toMillis(7)
        val ELAPSED_TIME_LIMIT_FOR_AUTO_LOGIN = TimeUnit.DAYS.toMillis(1)
    }
}