package org.alsi.android.domain.vod.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.vod.repository.VodRepository
import javax.inject.Inject

class SeriesVideoStreamUseCase  @Inject constructor(
    private val presentationManager: PresentationManager,
    executionThread: PostExecutionThread
): SingleObservableUseCase<VideoStream, SeriesVideoStreamUseCase.Params?>(executionThread) {

    override fun buildUseCaseObservable(params: Params?): Single<VideoStream> {
        params?: return Single.error(Throwable("No parameters to VodListingPageUseCase!"))
        val repo = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)?.directory
        return if (repo is VodRepository) {
            repo.getSeriesVideoStream(params.seriesId)
        }
        else {
            Single.error(Throwable("VOD Directory is N/A"))
        }
    }

    class Params (
        val seriesId: Long,
    )
}