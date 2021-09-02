package org.alsi.android.domain.vod.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
import org.alsi.android.domain.vod.repository.VodRepository
import javax.inject.Inject

class VodListingPageUseCase  @Inject constructor(
    private val presentationManager: PresentationManager,
    executionThread: PostExecutionThread
): SingleObservableUseCase<VodListingPage, VodListingPageUseCase.Params?>(executionThread) {

    override fun buildUseCaseObservable(params: Params?): Single<VodListingPage> {
        params?: return Single.error(Throwable("No parameters to VodListingPageUseCase!"))
        val repo = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)?.directory
        return if (repo is VodRepository) {
            repo.getListingPage(
                params.sectionId,
                params.unitId,
                params.page,
                params.count
            )
        }
        else {
            Single.error(Throwable("VOD Directory is N/A"))
        }
    }

    class Params (
        val sectionId: Int,
        val unitId: Int,
        val page: Int,
        val count: Int
    )
}