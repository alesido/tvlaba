package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.streaming.interactor.StreamingSettingsUseCase
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.domain.tv.interactor.guide.TvCurrentPlaybackUseCase
import org.alsi.android.domain.tv.interactor.guide.TvNewPlaybackUseCase
import org.alsi.android.domain.tv.interactor.guide.TvNextPlayback
import org.alsi.android.domain.tv.interactor.guide.TvNextPlaybackUseCase
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class TvPlaybackViewModel @Inject constructor(

        private val currentPlaybackUseCase: TvCurrentPlaybackUseCase,
        private val newPlaybackUseCase: TvNewPlaybackUseCase,
        private val nextPlaybackUseCase: TvNextPlaybackUseCase,
        private val getSettingsUseCase: StreamingSettingsUseCase

) : ViewModel() {

    val liveData: MutableLiveData<Resource<TvPlayback>> = MutableLiveData()

    var settings: StreamingServiceSettings? = null

    init {
        liveData.postValue(Resource.loading())
        currentPlaybackUseCase.execute(CurrentPlaybackSubscriber())
        getSettingsUseCase.execute(SettingsSubscriber())
    }

    fun getLiveData(): LiveData<Resource<TvPlayback>> = liveData


    fun onTvProgramIssueAction(item: TvProgramIssue) {
        liveData.postValue(Resource.loading())
        newPlaybackUseCase.execute(NewPlaybackSubscriber(),
                TvNewPlaybackUseCase.Params(0L, program = item))
    }

    fun onPreviousChannelAction() {
        nextPlaybackUseCase.execute(NewPlaybackSubscriber(),
                TvNextPlaybackUseCase.Params(TvNextPlayback.PREVIOUS_CHANNEL))
    }

    fun onNextChannelAction() {
        nextPlaybackUseCase.execute(NewPlaybackSubscriber(),
                TvNextPlaybackUseCase.Params(TvNextPlayback.NEXT_CHANNEL))
    }

    fun onPreviousProgramAction() {
        nextPlaybackUseCase.execute(NewPlaybackSubscriber(),
                TvNextPlaybackUseCase.Params(TvNextPlayback.PREVIOUS_PROGRAM))
    }

    fun onNextProgramAction() {
        nextPlaybackUseCase.execute(NewPlaybackSubscriber(),
                TvNextPlaybackUseCase.Params(TvNextPlayback.NEXT_PROGRAM))
    }

    fun dispose() {
        currentPlaybackUseCase.dispose()
        newPlaybackUseCase.dispose()
        nextPlaybackUseCase.dispose()
        getSettingsUseCase.dispose()
    }

    inner class CurrentPlaybackSubscriber: DisposableObserver<TvPlayback>() {
        override fun onNext(t: TvPlayback) {
            liveData.postValue(Resource.success(t))
        }
        override fun onComplete() {
            // seems not applicable
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource.error(e))
        }
    }

    inner class NewPlaybackSubscriber ()
        : DisposableSingleObserver<TvPlayback>() {
        override fun onSuccess(t: TvPlayback) {
            // current playback subscriber will get result too,
            // so avoid duplicate notification here
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource.error(e))
        }
    }

    inner class SettingsSubscriber ()
        : DisposableSingleObserver<StreamingServiceSettings>() {
        override fun onSuccess(t: StreamingServiceSettings) {
            settings = t
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource.error(e))
        }
    }
}