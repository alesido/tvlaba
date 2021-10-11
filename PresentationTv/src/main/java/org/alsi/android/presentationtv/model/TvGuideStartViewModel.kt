package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.streaming.interactor.SwitchPresentationContextUseCase
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.tv.interactor.guide.TvGetStartContextUseCase
import org.alsi.android.domain.tv.interactor.guide.TvRestoreBrowsingContextUseCase
import org.alsi.android.domain.tv.interactor.guide.TvRestorePlaybackUseCase
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvStartContext
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

open class TvGuideStartViewModel @Inject constructor (
    private val getStartContextUseCase: TvGetStartContextUseCase,
    private val switchPresentationContextUseCase: SwitchPresentationContextUseCase,
    private val restoreBrowsingContextUseCase: TvRestoreBrowsingContextUseCase,
    private val restorePlaybackUseCase: TvRestorePlaybackUseCase
) : ViewModel() {

    private val liveData: MutableLiveData<Resource<TvStartContext>> = MutableLiveData()

    private lateinit var startContext: TvStartContext

    fun getLiveData(): LiveData<Resource<TvStartContext>> = liveData

    fun initWithService(serviceId: Long?) {
        liveData.postValue(Resource.loading())
        switchPresentationContextUseCase.execute(SwitchPresentationContextSubscriber(),
            SwitchPresentationContextUseCase.Params(if (serviceId != null && serviceId != 0L)
                serviceId else StreamingService.DEFAULT_TV_ID))
    }

    inner class SwitchPresentationContextSubscriber: DisposableSingleObserver<StreamingService>() {
        override fun onSuccess(t: StreamingService) {
            getStartContextUseCase.execute(GetStartContextSubscriber())
        }
        override fun onError(e: Throwable) = liveData.postValue(Resource.error(e))
    }

    inner class GetStartContextSubscriber: DisposableSingleObserver<TvStartContext>() {
        override fun onSuccess(t: TvStartContext) {
            startContext = t
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
        }, TvRestoreBrowsingContextUseCase.Params(startContext.browse))
    }

    fun restorePlaybackContext() {
        restorePlaybackUseCase.execute(object: DisposableSingleObserver<TvPlayback>() {
            override fun onSuccess(t: TvPlayback) {
                liveData.postValue(Resource.success(startContext))
            }
            override fun onError(e: Throwable) {
                liveData.postValue(Resource.error(e))
            }
        }, TvRestorePlaybackUseCase.Params(startContext.play))
    }

    fun dispose() {
        getStartContextUseCase.dispose()
        switchPresentationContextUseCase.dispose()
        restoreBrowsingContextUseCase.dispose()
        restorePlaybackUseCase.dispose()
    }
}