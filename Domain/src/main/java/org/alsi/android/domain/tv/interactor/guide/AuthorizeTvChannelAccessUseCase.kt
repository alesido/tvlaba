package org.alsi.android.domain.tv.interactor.guide

import io.reactivex.Completable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import javax.inject.Inject

class AuthorizeTvChannelAccessUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread
)
    : CompletableUseCase<AuthorizeTvChannelAccessUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("ChangeParentalControlPinUseCase: Params can't be null!")

        val service =  presentationManager.provideContext(ServicePresentationType.TV_GUIDE)
        val directory = service?.directory
        if (directory !is TvDirectoryRepository)
            throw IllegalArgumentException("TvChannelDirectoryObservationUseCase: directory or session is N/A!")

        return directory.channels.authorizeContentAccess(params.parentalControlPass)
            .andThen (
                Completable.fromRunnable {
                    // make this valid password current
                    service.session.parentalControlPassword = params.parentalControlPass
                }
            )
    }

    class Params constructor (val parentalControlPass: String)
}