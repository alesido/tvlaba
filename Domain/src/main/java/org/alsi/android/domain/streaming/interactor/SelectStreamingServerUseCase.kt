package org.alsi.android.domain.streaming.interactor

import io.reactivex.Completable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import javax.inject.Inject

/**
 * Created on 7/18/18.
 */
class SelectStreamingServerUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : CompletableUseCase<SelectStreamingServerUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("SelectStreamingServerUseCase: Params can't be null!")
        val configuration = presentationManager.provideContext(
            presentationType = params.servicePresentationType
        )?.configuration
        return configuration!!.selectServer(params.serverTag)
    }

    class Params constructor (
        val servicePresentationType: ServicePresentationType,
        val serverTag: String)
}