package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Observable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.ObservableUseCase
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import javax.inject.Inject

open class TvChannelsUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        executionThread: PostExecutionThread)
    : ObservableUseCase<List<TvChannel>, TvChannelsUseCase.Params>(executionThread)
{
    override fun buildUseCaseObservable(params: Params?): Observable<List<TvChannel>> {
        params?: throw IllegalArgumentException("TvChannelsUseCase: Params can't be null!")

        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        return if (directory is TvDirectoryRepository) {
            directory.channels.getChannels(params.categoryId)
        }
        else {
            Observable.error(Throwable("TV Service Repository is N/A!"))
        }
    }

    class Params constructor (val categoryId: Long)
}
