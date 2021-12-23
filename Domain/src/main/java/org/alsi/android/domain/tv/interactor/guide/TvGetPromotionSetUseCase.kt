package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.model.guide.TvPromotionSet
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import javax.inject.Inject

class TvGetPromotionSetUseCase @Inject constructor (
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread)
    :SingleObservableUseCase<TvPromotionSet, Nothing?> (postExecutionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Single<TvPromotionSet> {
        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        if (directory !is TvDirectoryRepository)
            return Single.error(Throwable("TV Channel Directory is N/A!"))
        return directory.promotions.getPromotionSet()
    }
}