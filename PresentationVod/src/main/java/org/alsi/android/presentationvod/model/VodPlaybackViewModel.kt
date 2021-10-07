package org.alsi.android.presentationvod.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.streaming.interactor.StreamingSettingsUseCase
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.domain.vod.interactor.VodCurrentPlaybackUseCase
import org.alsi.android.domain.vod.interactor.VodNewPlaybackUseCase
import org.alsi.android.domain.vod.interactor.VodNextSeriesPlaybackUseCase
import org.alsi.android.domain.vod.interactor.VodUpdatePlaybackCursorUseCase
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class VodPlaybackViewModel @Inject constructor(
    private val currentPlaybackUseCase: VodCurrentPlaybackUseCase,
    private val newPlaybackUseCase: VodNewPlaybackUseCase,
    private val nextSeriesPlaybackUseCase: VodNextSeriesPlaybackUseCase,
    private val updatePlaybackCursorUseCase: VodUpdatePlaybackCursorUseCase,
    private val getSettingsUseCase: StreamingSettingsUseCase

) : ViewModel() {

    private val liveData: MutableLiveData<Resource<VodPlayback>> = MutableLiveData()

    init {
        liveData.postValue(Resource.loading())
        currentPlaybackUseCase.execute(CurrentPlaybackSubscriber())
    }

    fun getLiveData(): LiveData<Resource<VodPlayback>> = liveData

    fun dispose() {
        currentPlaybackUseCase.dispose()
        newPlaybackUseCase.dispose()
        nextSeriesPlaybackUseCase.dispose()
        updatePlaybackCursorUseCase.dispose()
        getSettingsUseCase.dispose()
    }

    //region View Model Interface

    fun getSettings(receiver: (settings: StreamingServiceSettings) -> Unit) {
        getSettingsUseCase.execute(SettingsSubscriber(receiver))
    }

    fun onListingItemAction(item: VodListingItem) {
        liveData.postValue(Resource.loading())
        newPlaybackUseCase.execute(NewPlaybackSubscriber(),
            VodNewPlaybackUseCase.Params(item))
    }

    fun onSeriesItemAction(item: VodListingItem, seriesId: Long) {
        liveData.postValue(Resource.loading())
        newPlaybackUseCase.execute(NewPlaybackSubscriber(),
            VodNewPlaybackUseCase.Params(item, seriesId))
    }

    fun onNextSeriesItemAction(item: VodListingItem, currentSeriesId: Long) {
        liveData.postValue(Resource.loading())
        nextSeriesPlaybackUseCase.execute(NewPlaybackSubscriber(),
            VodNextSeriesPlaybackUseCase.Params(item, currentSeriesId))
    }

    fun recordPlaybackState(currentPosition: Long) {
        liveData.value?.data?.let { currentPlayback ->
            currentPlayback.position = currentPosition
            updatePlaybackCursorUseCase.execute(
                object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        // do not reflect this in the user interface
                    }
                    override fun onError(e: Throwable) {
                        // not critical error
                    }
                },
                VodUpdatePlaybackCursorUseCase.Params(currentPlayback)
            )
        }
    }

    fun onPlayCompleted(fallback: () -> Boolean?) {
        // TODO Start next series playback if available, fallback otherwise
    }

    //endregion
    //region Subscribers

    inner class CurrentPlaybackSubscriber: DisposableObserver<VodPlayback>() {
        override fun onNext(t: VodPlayback) = liveData.postValue(Resource.success(t))
        override fun onComplete() { /** seems not applicable */ }
        override fun onError(e: Throwable) = liveData.postValue(Resource.error(e))
    }

    inner class NewPlaybackSubscriber ()
        : DisposableSingleObserver<VodPlayback>() {
        override fun onSuccess(t: VodPlayback) {
            // current playback subscriber will get result too,
            // so avoid duplicate notification here
        }
        override fun onError(e: Throwable) = liveData.postValue(Resource.error(e))
    }

    inner class SettingsSubscriber(val receiver: (settings: StreamingServiceSettings) -> Unit)
        : DisposableObserver<StreamingServiceSettings>() {
        override fun onNext(t: StreamingServiceSettings) = receiver(t)
        override fun onComplete() { /** not applicable */ }
        override fun onError(e: Throwable) = liveData.postValue(Resource.error(e))
    }

    //endregion
}