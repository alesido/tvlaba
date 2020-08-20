package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import javax.inject.Inject

class TvWeekDayRangeUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<TvWeekDayRange, Nothing?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Single<TvWeekDayRange> {
        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        val session = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.session
        if (directory !is TvDirectoryRepository || session !is TvSessionRepository)
            return Single.error(Throwable("The TV Directory Repository is N/A!"))
        return directory.programs.getScheduleWeekDayRange()
    }
}
