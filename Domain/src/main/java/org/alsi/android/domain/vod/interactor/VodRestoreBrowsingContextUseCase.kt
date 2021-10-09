package org.alsi.android.domain.vod.interactor

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.session.VodBrowseCursorReference
import org.alsi.android.domain.vod.model.session.VodBrowsePage
import org.alsi.android.domain.vod.model.session.VodSessionRepository
import org.alsi.android.domain.vod.repository.VodRepository
import javax.inject.Inject

class VodRestoreBrowsingContextUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread
) : CompletableUseCase<VodRestoreBrowsingContextUseCase.Params?>(postExecutionThread) {

    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("TvRestoreBrowsingContext: Params can't be null!")

        val ref = params.browseCursorReference
        val service = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)
        val directory = service?.directory
        val session = service?.session

        if (directory !is VodRepository || session !is VodSessionRepository)
            throw IllegalArgumentException("TvRestoreBrowsingContext: directory or session is N/A!")

        return directory.getDirectory().flatMap { dir ->

            if (dir.sections.isEmpty() ) return@flatMap Single.error(Throwable(
                    "Cannot restore browsing context on empty VOD directory"))

            val section = if (!ref.isEmpty()) dir.sectionById[ref.sectionId]
                else dir.sections[0]

            if (section?.units?.isEmpty() != false) return@flatMap Single.error(Throwable(
                    "Cannot restore browsing context on a corrupted VOD directory"))

            val unit = if (!ref.isEmpty()) section.unitById[ref.unitId]
                else section.units.elementAt(0)

            val item: VodListingItem? = if (!ref.isEmpty()) {
                unit?.window?.items?.first { it.id == ref.itemId }
            }
            else {
                if (unit?.window?.items?.isEmpty() == false)
                    unit.window?.items?.elementAt(0) else null
            }

            session.browse.moveCursorTo(section, unit, item,
                if (!ref.isEmpty()) ref.itemPosition else 0,
                if (!ref.isEmpty()) ref.page else VodBrowsePage.SECTIONS
            )

            // TODO Load listing window to contain the item if required

        }.flatMapCompletable {
            Completable.complete()
        }
    }

    class Params(val browseCursorReference: VodBrowseCursorReference)
}