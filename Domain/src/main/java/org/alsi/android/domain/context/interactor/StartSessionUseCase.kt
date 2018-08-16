package org.alsi.android.domain.context.interactor

import io.reactivex.Completable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import javax.inject.Inject

open class StartSessionUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        executionThread: PostExecutionThread)
    : CompletableUseCase<StartSessionUseCase.Params>(executionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("StartSessionUseCase: Params can't be null!")
//        val service = presentationManager.registry.serviceById[params.serviceId]?:
        throw IllegalArgumentException("StartSessionUseCase: Requested service is not found")
        return Completable.complete()
//        return service.account.login(params.loginName, params.loginPassword)
//                .flatMapCompletable {
//                    presentationManager.selectContext(service.programId)
//                    Completable.complete()
//                }
    }

    class Params constructor (val loginName: String, val loginPassword: String, val serviceId: Long)
}