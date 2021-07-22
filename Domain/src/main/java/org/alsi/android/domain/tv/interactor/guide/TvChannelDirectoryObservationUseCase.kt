package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Observable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.ObservableUseCase
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import javax.inject.Inject

open class TvChannelDirectoryObservationUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : ObservableUseCase<TvChannelDirectory, Nothing?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Observable<TvChannelDirectory> {
        val directory = presentationManager.provideContext(ServicePresentationType.TV_GUIDE)?.directory
        return if (directory is TvDirectoryRepository) {
            // get channel directory as an observation subject
            directory.channels.observeDirectory()
        }
        else {
            Observable.error(Throwable("TV Channel Directory is N/A!"))
        }
    }
}
