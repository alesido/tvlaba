package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.tv.model.session.TvBrowseCursorReference
import org.alsi.android.domain.tv.model.session.TvBrowsePage
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import org.joda.time.LocalDate
import javax.inject.Inject

/** Browse cursor stored as a reference to category, channel, schedule and program issue,
 *  i.e. full browsing context. This US is to restore the data from a reference.
 *
 *  At the same time browse cursor may not reference any category, channel, schedule
 *  and program issue because it was a menu item selected last time.
 *
 */
class TvRestoreBrowsingContextUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread
) : CompletableUseCase<TvRestoreBrowsingContextUseCase.Params?>(postExecutionThread) {

    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("TvRestoreBrowsingContext: Params can't be null!")

        val ref = params.browseCursorReference
        val service = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)
        val directory = service?.directory
        val session = service?.session

        if (directory !is TvDirectoryRepository || session !is TvSessionRepository)
            throw IllegalArgumentException("TvRestoreBrowsingContext: directory or session is N/A!")

        if (ref.isMenuItemReference()) {
            return Completable.complete()
        }

        return directory.channels.getDirectory().flatMap { dir ->

            if (dir.channels.isEmpty() || dir.categories.isEmpty())
                return@flatMap Single.error(Throwable("Cannot restore browsing context on empty channels directory"))

            val channel = if (!ref.isEmpty()) dir.channelById[ref.channelId]
                else dir.channels[0]

            if (null == channel?.categoryId || null == dir.categoryById[channel.categoryId])
                return@flatMap Single.error(Throwable("Cannot restore browsing context on corrupted channels directory"))

            val category = if (!ref.isEmpty()) dir.categoryById[ref.categoryId]
                else dir.categoryById[channel.categoryId]

            session.browse.moveCursorTo(
                category = category,
                channel = if (!ref.isEmpty()) channel else null, // to focus on category initially
                page = if (!ref.isEmpty()) params.browseCursorReference.page else TvBrowsePage.CHANNELS)
            Single.just(dir)

        }.flatMap { dir ->

            val channelId = if (!ref.isEmpty()) ref.channelId else dir.channels[0].id
            directory.programs.getDaySchedule(channelId, ref.scheduleDate?: LocalDate.now())

        }.flatMap {

            session.browse.moveCursorTo(
                schedule = it,
                program = if (ref.programId != null) it.programById(ref.programId) else null,
                reuse = true
            )
        }.flatMapCompletable {
            Completable.complete()
        }
    }

    class Params(val browseCursorReference: TvBrowseCursorReference)
}