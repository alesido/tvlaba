package org.alsi.android.domain.streaming.interactor

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.implementation.interactor.SingleObservableUseCase
import javax.inject.Inject

class GetSessionParentalControlPasswordUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread
)
    : SingleObservableUseCase<String, Nothing?>(postExecutionThread)
{
    override fun buildUseCaseObservable(params: Nothing?): Single<String> {
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "GetParentalControlPasswordUseCase: Service context isn't initialized!")
        return Single.just(context.session.parentalControlPassword?: "")
    }
}

class SetSessionParentalControlPasswordUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread
) : CompletableUseCase<SetSessionParentalControlPasswordUseCase.Params>(postExecutionThread) {

    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("SetParentalControlPasswordUseCase: Params can't be null!")
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "SetParentalControlPasswordUseCase: Service context isn't initialized!")
        context.session.parentalControlPassword = params.parentalControlPassword
        return Completable.complete()
    }

    class Params (val parentalControlPassword: String)
}

class DropSessionParentalControlPasswordUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread
) : CompletableUseCase<Nothing?>(postExecutionThread) {

    override fun buildUseCaseCompletable(params: Nothing?): Completable {
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "DropParentalControlPasswordUseCase: Service context isn't initialized!")
        context.session.parentalControlPassword = null
        return Completable.complete()
    }
}