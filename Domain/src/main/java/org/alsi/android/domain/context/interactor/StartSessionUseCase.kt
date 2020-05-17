package org.alsi.android.domain.context.interactor

import io.reactivex.Completable
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.streaming.model.ServiceProvider
import javax.inject.Inject

open class  StartSessionUseCase @Inject constructor(
        private val provider: ServiceProvider,
        executionThread: PostExecutionThread)
    : CompletableUseCase<StartSessionUseCase.Params>(executionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("StartSessionUseCase: Params can't be null!")
        return provider.accountService.login(params.loginName, params.loginPassword).ignoreElement()
    }

    class Params constructor (val loginName: String, val loginPassword: String)
}