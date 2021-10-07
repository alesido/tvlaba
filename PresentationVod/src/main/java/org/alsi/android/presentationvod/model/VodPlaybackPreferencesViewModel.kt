package org.alsi.android.presentationvod.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.streaming.interactor.StreamingSettingsUseCase
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.presentation.model.LanguageTrackSelection
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class VodPlaybackPreferencesViewModel @Inject constructor(
        getSettingsUseCase: StreamingSettingsUseCase
) : ViewModel() {

    private val changeLiveData:
            MutableLiveData<Resource<VodPlaybackPreferenceChangeEvent>> = MutableLiveData()

    init {
        changeLiveData.postValue(Resource.loading())
        getSettingsUseCase.execute(SettingsSubscriber())
    }

    var trackLanguageSelection: LanguageTrackSelection? = null
    set(value) {
        field = value
        field?.preferredLanguage = settings?.language
    }

    var settings: StreamingServiceSettings? = null


    fun getPreferenceChangeLiveData() = changeLiveData

    fun onAspectRatioChanged(aspectRatio: VideoAspectRatio) {
        val event = VodPlaybackAspectRatioChanged(aspectRatio)
        changeLiveData.value = Resource.success(event)
    }

    fun onAudioTrackLanguageSelected(selectedIndex: Int) {
        trackLanguageSelection?.selectAudioTrack(selectedIndex)
    }

    fun onTextTrackLanguageSelected(selectedIndex: Int) {
        trackLanguageSelection?.selectTextTrack(selectedIndex)
    }

    fun turnSubtitlesOff() {
        trackLanguageSelection?.turnSubtitlesOff()
    }

    inner class SettingsSubscriber()
        : DisposableObserver<StreamingServiceSettings>() {
        override fun onNext(t: StreamingServiceSettings) {
            settings = t
            trackLanguageSelection?.let {
                it.preferredLanguage = settings?.language
            }
        }
        override fun onComplete() { /** not applicable */ }
        override fun onError(e: Throwable) = changeLiveData.postValue(Resource.error(e))
    }
}