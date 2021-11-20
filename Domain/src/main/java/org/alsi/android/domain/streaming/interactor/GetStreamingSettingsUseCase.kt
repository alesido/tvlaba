package org.alsi.android.domain.streaming.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import javax.inject.Inject

class GetStreamingSettingsUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<StreamingServiceSettings, Nothing?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params:Nothing?): Single<StreamingServiceSettings> {
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "GetStreamingSettingsUseCase: Service context isn't initialized!")
        return Single.fromCallable {
            context.configuration.lastValues()
        }
    }
}