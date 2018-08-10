package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Observable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.interactor.ObservableUseCase
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import javax.inject.Inject

open class TvCategoriesUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : ObservableUseCase<List<TvChannelCategory>, Nothing?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Observable<List<TvChannelCategory>> {
        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        return if (directory is TvDirectoryRepository) {
            directory.channels.getCategories()
        }
        else {
            Observable.error(Throwable("TV Service Repository is N/A!"))
        }
    }
}
