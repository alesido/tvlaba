package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvBrowsePage
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

open class TvBrowseCursorMoveUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        executionThread: PostExecutionThread)
    : SingleObservableUseCase<TvBrowseCursor, TvBrowseCursorMoveUseCase.Params?>(executionThread)
{
    override fun buildUseCaseObservable(params: Params?): Single<TvBrowseCursor> {

        if (null == params) return Single.error(Throwable("No parameters to get playback data!"))

        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (session !is TvSessionRepository)
            return Single.error(Throwable("TV Service Repository is N/A!"))

        with(params) {
            return session.browse.moveCursorTo(
                    category = category,
                    channel = channel,
                    schedule = schedule,
                    program = program,
                    page = page,
                    reuse = reuse
            )
        }
    }

    class Params constructor (
            val category: TvChannelCategory? = null,
            val channel: TvChannel? = null,
            val schedule: TvDaySchedule? = null,
            val program: TvProgramIssue? = null,
            val page: TvBrowsePage? = null,
            val reuse: Boolean = false
    )
}
