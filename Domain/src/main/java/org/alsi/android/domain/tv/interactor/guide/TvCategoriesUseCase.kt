package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Observable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.interactor.ObservableUseCase
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.model.IconSet
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.repository.guide.TvChannelRepository
import javax.inject.Inject

open class TvCategoriesUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : ObservableUseCase<List<TvChannelCategory>, Nothing?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Observable<List<TvChannelCategory>> {
        val serviceRepository = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        return if (serviceRepository is TvChannelRepository) {
            serviceRepository.getCategories()
        }
        else {
            Observable.error(Throwable("TV Service Repository isn't available!"))
        }
    }
}

open class TvCategoryIconsUseCase @Inject constructor(
//        private val tvChannelRepository: TvChannelRepository,
        postExecutionThread: PostExecutionThread)
    : ObservableUseCase<IconSet, Nothing?>(postExecutionThread) {
    override fun buildUseCaseObservable(params: Nothing?): Observable<IconSet> {
        return Observable.just(null) //tvChannelRepository.getCategoryIcons()
    }
}
