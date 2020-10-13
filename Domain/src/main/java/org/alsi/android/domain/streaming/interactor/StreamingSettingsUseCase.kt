package org.alsi.android.domain.streaming.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.domain.tv.model.guide.TvPlaybackMapper
import javax.inject.Inject

/**
 * Created on 7/18/18.
 */
class StreamingSettingsUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<StreamingServiceSettings, Nothing?>(postExecutionThread)
{
    private val mapper = TvPlaybackMapper()

    override fun buildUseCaseObservable(params: Nothing?): Single<StreamingServiceSettings> {
        val configuration = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)
                ?.configuration
        return Single.just(configuration?.values())
    }
}