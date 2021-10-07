package org.alsi.android.domain.streaming.interactor

import io.reactivex.Observable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.ObservableUseCase
import org.alsi.android.domain.streaming.model.service.StreamingServiceProfile
import javax.inject.Inject

class StreamingProfileUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread
)
    : ObservableUseCase<StreamingServiceProfile,
        StreamingProfileUseCase.Params?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Params?): Observable<StreamingServiceProfile> {
        val configuration = presentationManager.provideContext(
            presentationType = params?.servicePresentationType?: ServicePresentationType.TV_GUIDE
        )?.configuration
        return configuration!!.profile()
    }

    class Params constructor (val servicePresentationType: ServicePresentationType)
}