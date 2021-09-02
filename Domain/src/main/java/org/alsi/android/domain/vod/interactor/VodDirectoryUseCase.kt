package org.alsi.android.domain.vod.interactor

import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import org.alsi.android.domain.vod.model.guide.directory.VodDirectory
import org.alsi.android.domain.vod.repository.VodRepository
import javax.inject.Inject

class VodDirectoryUseCase  @Inject constructor(
    private val presentationManager: PresentationManager,
    executionThread: PostExecutionThread
): SingleObservableUseCase<VodDirectory, Nothing?>(executionThread) {

    override fun buildUseCaseObservable(params: Nothing?): Single<VodDirectory> {
        val repo = presentationManager.provideContext(ServicePresentationType.VOD_GUIDE)?.directory
        return if (repo is VodRepository) {
            // get channel directory as an observation subject
            repo.getDirectory()
        }
        else {
            Single.error(Throwable("VOD Directory is N/A!"))
        }
    }
}