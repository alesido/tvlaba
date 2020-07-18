package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Completable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.tv.model.guide.TvChannelListWindow
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import javax.inject.Inject

open class TvChannelDirectoryActualizationUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : CompletableUseCase<TvChannelDirectoryActualizationUseCase.Params?>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("StartSessionUseCase: Params can't be null!")
        return Completable.fromRunnable{
            val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
            if (directory is TvDirectoryRepository) {
                directory.channels.scheduleChannelsUpdate(params.window)
            }
        }
    }

    class Params constructor (val window: TvChannelListWindow)
}
