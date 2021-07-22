package org.alsi.android.domain.context.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.SessionActivityType
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.ServiceProvider
import org.alsi.android.domain.streaming.model.service.StreamingServiceRegistry
import org.joda.time.DateTimeUtils
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 *  Attempt to resume user session by skipping login or making auto-login if the
 *  last session is quite recent. Require user to login, if nobody logged in yet,
 *  or the last user activity was too long ago.
 */
open class  TryResumeSessionUseCase @Inject constructor(
    private val registry: StreamingServiceRegistry,
    private val presentationManager: PresentationManager,
    private val provider: ServiceProvider,
    executionThread: PostExecutionThread)
    : SingleObservableUseCase<SessionActivityType, Nothing?>(executionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Single<SessionActivityType> {

        return Single.zip(registry.map { it.session.mostRecentActivity(it.id) }) { zipArray ->
            val nonEmpties = zipArray.filter { !(it as UserActivityRecord).isEmpty() }
            if (nonEmpties.isEmpty())
                UserActivityRecord.empty()
            else
                nonEmpties.sortedByDescending { (it as UserActivityRecord).timeStamp }[0]
        }.flatMap {
            val activity = it as UserActivityRecord
            if (activity.isEmpty())
                return@flatMap Single.just(SessionActivityType.LOGIN)
            return@flatMap resumeActivity(activity)
        }
    }

    private fun resumeActivity(activity: UserActivityRecord): Single<SessionActivityType> {
        val nowTimeStamp = DateTimeUtils.currentTimeMillis()
        return when {
            nowTimeStamp - activity.timeStamp < ELAPSED_TIME_LIMIT_FOR_AUTO_LOGIN -> {
                presentationManager.switchToContext(activity.serviceId)
                provider.accountService.resume(activity.loginName, true)
                    .map { activity.activityType }
            }
            nowTimeStamp - activity.timeStamp < ELAPSED_TIME_LIMIT_FOR_MANUAL_LOGIN -> {
                presentationManager.switchToContext(activity.serviceId)
                provider.accountService.resume(activity.loginName, false)
                    .map { activity.activityType }
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