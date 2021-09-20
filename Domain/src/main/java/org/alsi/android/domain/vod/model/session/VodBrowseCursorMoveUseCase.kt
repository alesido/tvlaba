package org.alsi.android.domain.vod.model.session

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import javax.inject.Inject

open class VodBrowseCursorMoveUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        executionThread: PostExecutionThread)
    : SingleObservableUseCase<VodBrowseCursor, VodBrowseCursorMoveUseCase.Params?>(executionThread)
{
    override fun buildUseCaseObservable(params: Params?): Single<VodBrowseCursor> {

        if (null == params) return Single.error(Throwable("No parameters to move VOD browsing cursor!"))

        val session = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)?.session
        if (session !is VodSessionRepository)
            return Single.error(Throwable("VOD Service Repository is N/A!"))

        with(params) {
            return session.browse.moveCursorTo(
                    section = section,
                    unit = unit,
                    item = item,
                    itemPosition = itemPosition,
                    page = page,
                    reuse = reuse
            )
        }
    }

    class Params constructor (
        val section: VodSection? = null,
        val unit: VodUnit? = null,
        val item: VodListingItem? = null,
        val itemPosition: Int? = null,
        val page: VodBrowsePage? = null,
        val reuse: Boolean = false
    )
}
