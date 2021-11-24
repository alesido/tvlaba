package org.alsi.android.domain.user

import io.reactivex.Completable
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.streaming.model.ServiceProvider
import javax.inject.Inject

class SetRememberMeAtLoginUseCase @Inject constructor(
    private val provider: ServiceProvider,
    executionThread: PostExecutionThread
)
    : CompletableUseCase<SetRememberMeAtLoginUseCase.Params>(executionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable
    = provider.accountService.setRememberMeAtLogin(params?.rememberMeAtLogin?: false)


    class Params constructor (val rememberMeAtLogin: Boolean)
}