package org.alsi.android.domain.context.interactor

import io.reactivex.Single
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.ServiceProvider
import org.alsi.android.domain.user.model.UserAccount
import javax.inject.Inject

open class  LastSessionAccountUseCase @Inject constructor(
        private val provider: ServiceProvider,
        executionThread: PostExecutionThread)
    : SingleObservableUseCase<UserAccount, Nothing?>(executionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Single<UserAccount> {
        return provider.accountService.getAccount()
    }
}