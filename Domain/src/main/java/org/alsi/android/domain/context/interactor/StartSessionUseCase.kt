package org.alsi.android.domain.context.interactor

import io.reactivex.Completable
import org.alsi.android.domain.context.model.PresentationContext
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.user.repository.AccountService
import javax.inject.Inject

/**
 * Created on 7/15/18.
 */
open class StartSessionUseCase @Inject constructor(
        private val serviceId: String,
        private val presentationManager: PresentationManager,
        executionThread: PostExecutionThread)
    : CompletableUseCase<StartSessionUseCase.Params>(executionThread)
{
    private val configuration = presentationManager.getServiceConfiguration(serviceId)
    private var accountService: AccountService? = configuration?.account

    override fun buildUseCaseCompletable(params: Params?): Completable {
        if (params == null) throw IllegalArgumentException("StartSessionUseCase params can't be null!")
        return accountService
                ?.login(params.loginName, params.loginPassword)
                ?.flatMapCompletable { account -> configuration?.let {

                val session = it.session.getSession(account, serviceId)

                val context = PresentationContext(
                        serviceId,
                        it.type,
                        account,
                        it.repository.withSession(session),
                        session,
                        it.settings.getPersonalSettings(account),
                        it.device.getPersonalSettings(account))

                    presentationManager.setPresentationContext(serviceId, context)

                Completable.complete()
            }?: Completable.error(Throwable("No service configuration provided!"))
        }?: Completable.error(Throwable("Account service is not available!"))
    }

    private fun configureSession() : Completable? {
        return accountService?.getSubscriptions()?.toCompletable()
    }

    class Params constructor (val loginName: String, val loginPassword: String)
}