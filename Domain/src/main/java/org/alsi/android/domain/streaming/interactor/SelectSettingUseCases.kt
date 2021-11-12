package org.alsi.android.domain.streaming.interactor

import io.reactivex.Completable
import org.alsi.android.domain.context.model.PresentationManager
import org.alsi.android.domain.implementation.executor.PostExecutionThread
import org.alsi.android.domain.implementation.interactor.CompletableUseCase
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import javax.inject.Inject

class SelectLanguageUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread)
    : CompletableUseCase<SelectLanguageUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("SelectLanguageUseCase: Params can't be null!")
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "SelectLanguageUseCase: Service context isn't initialized!")

        with (context.directory) {
            if (this !is TvDirectoryRepository)
                throw IllegalStateException("SelectLanguageUseCase: illegal directory repository!")

            return context.configuration.selectLanguage(params.languageCode)
                .andThen ( channels.onLanguageChange() )
        }
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

class ChangeParentalControlPasswordUseCase @Inject constructor(
    private val presentationManager: PresentationManager,
    postExecutionThread: PostExecutionThread)
    : CompletableUseCase<ChangeParentalControlPasswordUseCase.Params>(postExecutionThread)
{
    override fun buildUseCaseCompletable(params: Params?): Completable {
        params?: throw IllegalArgumentException("ChangeParentalControlPinUseCase: Params can't be null!")
        val context = presentationManager.provideContext()?: throw IllegalArgumentException(
            "ChangeParentalControlPinUseCase: Service context isn't initialized!")
        return context.configuration.changeParentalControlPassword(params.currentPass, params.newPass)
    }

    class Params constructor (val currentPass: String, val newPass: String)
}

