package org.alsi.android.domain.vod.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.domain.vod.model.guide.playback.VodPlaybackMapper
import org.alsi.android.domain.vod.model.session.VodSessionRepository
import org.alsi.android.domain.vod.repository.VodRepository
import javax.inject.Inject

class VodNextSeriesPlaybackUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread
) : SingleObservableUseCase<VodPlayback, VodNextSeriesPlaybackUseCase.Params>(postExecutionThread) {

    private val mapper = VodPlaybackMapper()

    override fun buildUseCaseObservable(params: Params?): Single<VodPlayback> {

        if (null == params) return Single.error(Throwable("No parameters to get playback data!"))

        val directory = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)?.directory
        val session = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)?.session
        if (directory !is VodRepository || session !is VodSessionRepository)
            return Single.error(Throwable("The VOD directory repository is not available"))

        with(params) {

            if (vod.video !is VodListingItem.Video.Serial)
                return Single.error(Throwable("The VOD is not a serial!"))

            val currentSeriesIndex = vod.video.series.indexOfFirst { it.id == currentSeriesId }
            if (currentSeriesIndex < 0 || currentSeriesIndex > vod.video.series.size - 2)
                return Single.just(VodPlayback.empty()) // There is no next series!

            val nextSeries = vod.video.series[currentSeriesIndex + 1]

            return directory.getSeriesVideoStream(nextSeries.id)
                .map { mapper.from(params.vod, it, nextSeries.id) }
                .flatMap { session.play.setCursorTo(it) }
        }
    }

    class Params constructor (val vod: VodListingItem, val currentSeriesId: Long)
}