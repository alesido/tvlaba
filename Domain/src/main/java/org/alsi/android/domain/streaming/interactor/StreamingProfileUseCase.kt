package org.alsi.android.domain.streaming.interactor

import io.reactivex.Observable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.ObservableUseCase
import org.alsi.android.domain.streaming.model.service.StreamingServiceProfile
import javax.inject.Inject

class StreamingProfileUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread
)
    : ObservableUseCase<StreamingServiceProfile, Nothing?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Observable<StreamingServiceProfile> {
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "StreamingProfileUseCase: Service context isn't initialized!")
        return context.configuration.profile()
    }
}