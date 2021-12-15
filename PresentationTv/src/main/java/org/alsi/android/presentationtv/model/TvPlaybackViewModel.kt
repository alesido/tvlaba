package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.streaming.interactor.StreamingSettingsUseCase
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.domain.tv.interactor.guide.*
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class TvPlaybackViewModel @Inject constructor(

        private val currentPlaybackUseCase: TvCurrentPlaybackUseCase,
        private val authorizePlaybackUseCase: TvAuthorizePlaybackUseCase,
        private val newPlaybackUseCase: TvNewPlaybackUseCase,
        private val nextPlaybackUseCase: TvNextPlaybackUseCase,
        private val liveRecordStreamUseCase: TvLiveRecordStreamUseCase,
        private val regetLiveStreamUseCase: TvRegetLiveStreamUseCase,
        private val updatePlaybackCursorUseCase: TvUpdatePlaybackCursorUseCase,
        private val getSettingsUseCase: StreamingSettingsUseCase

) : ViewModel() {

    private val liveData: MutableLiveData<Resource<TvPlayback>> = MutableLiveData()

    /** To obtain record/archive stream for live in a separate work flow.
     */
    private val liveRecordStreamLiveData: MutableLiveData<Resource<VideoStream>> = MutableLiveData()
    private val liveStreamLiveData: MutableLiveData<Resource<VideoStream>> = MutableLiveData()

    init {
        liveData.postValue(Resource.loading())
        currentPlaybackUseCase.execute(CurrentPlaybackSubscriber())
    }

    fun getLiveData(): LiveData<Resource<TvPlayback>> = liveData
    fun getLiveRecordStreamLiveData(): LiveData<Resource<VideoStream>> = liveRecordStreamLiveData
    fun getLiveStreamLiveData(): LiveData<Resource<VideoStream>> = liveStreamLiveData

    fun getSettings(receiver: (settings: StreamingServiceSettings) -> Unit) {
        getSettingsUseCase.execute(SettingsSubscriber(receiver))
    }

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

    fun onPlayCompleted(fallback: () -> Unit) {
        nextPlaybackUseCase.execute(ContinueToNextPlaybackSubscriber(fallback),
                TvNextPlaybackUseCase.Params(TvNextPlayback.NEXT_PROGRAM))
    }

    fun getLiveRecordStream(playback: TvPlayback) {
        liveRecordStreamUseCase.execute(object: DisposableSingleObserver<VideoStream>() {
            override fun onSuccess(t: VideoStream) {
                playback.record = t
                playback.isLiveRecord = true
                liveRecordStreamLiveData.postValue(Resource.success(t))
            }
            override fun onError(e: Throwable) = liveRecordStreamLiveData.postValue(Resource.error(e))
        }, TvLiveRecordStreamUseCase.Params(playback))
    }

    /** This is to follow the workflow "glue-model-fragment-glue". It is to not violate current
     *  responsibilities assignment.
     */
    fun regetLiveStream(playback: TvPlayback) {
        regetLiveStreamUseCase.execute(object: DisposableSingleObserver<VideoStream>() {
            override fun onSuccess(t: VideoStream) {
                playback.isLiveRecord = false
                liveStreamLiveData.postValue(Resource.success(t))
            }
            override fun onError(e: Throwable) = liveRecordStreamLiveData.postValue(Resource.error(e))
        }, TvRegetLiveStreamUseCase.Params(playback))

    }

    /** Update the playback cursor to current playback position and whether it paused
     */
    fun recordPlaybackState(seekTime: Long) {
        liveData.value?.data?.let { currentPlayback ->
            currentPlayback.position = seekTime
            updatePlaybackCursorUseCase.execute(
                object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        // do not reflect this in the user interface
                    }
                    override fun onError(e: Throwable) {
                        // not critical error
                    }
                },
                TvUpdatePlaybackCursorUseCase.Params(currentPlayback)
            )
        }
    }

    fun authorizePlayback(subject: TvPlayback) {
        liveData.postValue(Resource.loading())
        authorizePlaybackUseCase.execute(object : DisposableSingleObserver<TvPlayback>() {
            override fun onSuccess(t: TvPlayback) {
                // current playback subscriber will get result too,
                // so avoid duplicate notification here
            }
            override fun onError(e: Throwable) = liveData.postValue(Resource.error(e))
        }, TvAuthorizePlaybackUseCase.Params(subject))
    }

    fun dispose() {
        currentPlaybackUseCase.dispose()
        newPlaybackUseCase.dispose()
        nextPlaybackUseCase.dispose()
        liveRecordStreamUseCase.dispose()
        updatePlaybackCursorUseCase.dispose()
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

    inner class ContinueToNextPlaybackSubscriber (val fallback: () -> Unit)
        : DisposableSingleObserver<TvPlayback>() {
        override fun onSuccess(t: TvPlayback) {
            // current playback subscriber will get result too,
            // so avoid duplicate notification here
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource.error(e))
            fallback()
        }
    }

    inner class SettingsSubscriber(val receiver: (settings: StreamingServiceSettings) -> Unit)
        : DisposableObserver<StreamingServiceSettings>() {
        override fun onNext(t: StreamingServiceSettings) = receiver(t)
        override fun onComplete() { /** not applicable */ }
        override fun onError(e: Throwable) = liveData.postValue(Resource.error(e))
    }
}