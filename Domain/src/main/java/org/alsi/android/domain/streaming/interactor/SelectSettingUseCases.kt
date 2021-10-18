package org.alsi.android.domain.streaming.interactor

import io.reactivex.Completable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import javax.inject.Inject

class SelectLanguageUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread)
    : CompletableUseCase<SelectLanguageUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("SelectLanguageUseCase: Params can't be null!")
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "SelectStreamingServerUseCase: Service context isn't initialized!")
        return context.configuration.selectLanguage(params.languageCode)
    }

    class Params constructor (val languageCode: String)
}

class SelectStreamingServerUseCase @Inject constructor(
        private val presentationManager: PresentationManager,
        postExecutionThread: PostExecutionThread)
    : CompletableUseCase<SelectStreamingServerUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("SelectStreamingServerUseCase: Params can't be null!")
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "SelectStreamingServerUseCase: Service context isn't initialized!")
        return context.configuration.selectServer(params.serverTag)
    }

    class Params constructor (val serverTag: String)
}

class SelectCacheSizeUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread)
    : CompletableUseCase<SelectCacheSizeUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("SelectStreamingServerUseCase: Params can't be null!")
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "SelectStreamingServerUseCase: Service context isn't initialized!")
        return context.configuration.selectCacheSize(params.cacheSize)
    }

    class Params constructor (val cacheSize: Long)
}

class SelectStreamBitrateUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread)
    : CompletableUseCase<SelectStreamBitrateUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("SelectStreamBitrateUseCase: Params can't be null!")
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "SelectStreamingServerUseCase: Service context isn't initialized!")
        return context.configuration.selectStreamBitrate(params.bitrate)
    }

    class Params constructor (val bitrate: Int)
}

class SelectDeviceModelUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread)
    : CompletableUseCase<SelectDeviceModelUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("SelectDeviceModelUseCase: Params can't be null!")
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "SelectStreamingServerUseCase: Service context isn't initialized!")
        return context.configuration.selectDevice(params.deviceModelId.toString())
    }

    class Params constructor (val deviceModelId: Long)
}

class ChangeParentalControlPinUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread)
    : CompletableUseCase<ChangeParentalControlPinUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("ChangeParentalControlPinUseCase: Params can't be null!")
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "ChangeParentalControlPinUseCase: Service context isn't initialized!")
        return context.configuration.changeParentalControlPin(params.currentPin, params.newPin)
    }

    class Params constructor (val currentPin: String, val newPin: String)
}
