package org.alsi.android.presentationvod.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.context.interactor.StartSessionUseCase
import org.alsi.android.domain.streaming.interactor.SwitchPresentationContextUseCase
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.tv.interactor.guide.TvGetStartContextUseCase
import org.alsi.android.domain.tv.interactor.guide.TvRestoreBrowsingContextUseCase
import org.alsi.android.domain.tv.interactor.guide.TvRestorePlaybackUseCase
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvStartContext
import org.alsi.android.domain.vod.interactor.VodGetStartContextUseCase
import org.alsi.android.domain.vod.interactor.VodRestoreBrowsingContextUseCase
import org.alsi.android.domain.vod.interactor.VodRestorePlaybackUseCase
import org.alsi.android.domain.vod.model.guide.VodStartContext
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import javax.inject.Inject

open class VodGuideStartViewModel @Inject constructor (
    private val getStartContextUseCase: VodGetStartContextUseCase,
    private val switchPresentationContextUseCase: SwitchPresentationContextUseCase,
    private val restoreBrowsingContextUseCase: VodRestoreBrowsingContextUseCase,
    private val restorePlaybackUseCase: VodRestorePlaybackUseCase
) : ViewModel() {

    private val liveData: MutableLiveData<Resource<VodStartContext>> = MutableLiveData()

    private lateinit var startContext: VodStartContext

    fun getLiveData(): LiveData<Resource<VodStartContext>> = liveData

    init {
        liveData.postValue(Resource.loading())
        getStartContextUseCase.execute(GetStartContextSubscriber())
    }

    inner class GetStartContextSubscriber: DisposableSingleObserver<VodStartContext>() {
        override fun onSuccess(t: VodStartContext) {
            startContext = t
            switchPresentationContextUseCase.execute(SwitchPresentationContextSubscriber(),
                SwitchPresentationContextUseCase.Params(startContext.activity.serviceId))
        }
        override fun onError(e: Throwable) = liveData.postValue(Resource.error(e))
    }

    inner class SwitchPresentationContextSubscriber: DisposableSingleObserver<StreamingService>() {
        override fun onSuccess(t: StreamingService) {
            if (startContext.browse.isEmpty())
                liveData.postValue(Resource.success(startContext))
            else
                restoreBrowsingContext()
        }
        override fun onError(e: Throwable) = liveData.postValue(Resource.error(e))
    }

    fun restoreBrowsingContext() {
        restoreBrowsingContextUseCase.execute(object: DisposableCompletableObserver() {
            override fun onComplete() {
                if (startContext.play.isEmpty())
                    liveData.postValue(Resource.success(startContext))
                else
                    restorePlaybackContext()
            }
            override fun onError(e: Throwable) {
                liveData.postValue(Resource.error(e))
            }
        }, VodRestoreBrowsingContextUseCase.Params(startContext.browse))
    }

    fun restorePlaybackContext() {
        restorePlaybackUseCase.execute(object: DisposableSingleObserver<VodPlayback>() {
            override fun onSuccess(t: VodPlayback) {
                liveData.postValue(Resource.success(startContext))
            }
            override fun onError(e: Throwable) {
                liveData.postValue(Resource.error(e))
            }
        }, VodRestorePlaybackUseCase.Params(startContext.play))
    }

    fun dispose() {
        getStartContextUseCase.dispose()
        switchPresentationContextUseCase.dispose()
        restoreBrowsingContextUseCase.dispose()
        restorePlaybackUseCase.dispose()
    }
}