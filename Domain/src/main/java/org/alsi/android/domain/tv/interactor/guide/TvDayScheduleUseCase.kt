package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import org.joda.time.LocalDate
import javax.inject.Inject

class TvDayScheduleUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : SingleObservableUseCase<TvDaySchedule, TvDayScheduleUseCase.Params?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Params?): Single<TvDaySchedule> {

        if (null == params) return Single.error(Throwable("No parameters to get playback data!"))

        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        if (directory !is TvDirectoryRepository)
            return Single.error(Throwable("The TV Directory Repository is N/A!"))

        with(params) {
            return directory.programs.getDaySchedule(channelId, date?: LocalDate.now())
            .onErrorResumeNext {
                Single.just(TvDaySchedule(channelId, date?: LocalDate.now(), items = listOf()))
            }.flatMap {
                Single.just(it)
            }
        }
    }

    class Params constructor (val channelId: Long, val date: LocalDate? = null)
}
