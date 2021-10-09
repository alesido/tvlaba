package org.alsi.android.domain.streaming.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.service.StreamingService
import javax.inject.Inject

/**
 * Created on 7/18/18.
 */
class SwitchPresentationContextUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<StreamingService, SwitchPresentationContextUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Params?): Single<StreamingService> {
        params?: throw IllegalArgumentException("SelectStreamingServerUseCase: Params can't be null!")
        return Single.just(
            presentationManager.switchToContext(params.serviceId)
        )
    }

    class Params constructor (val serviceId: Long)
}