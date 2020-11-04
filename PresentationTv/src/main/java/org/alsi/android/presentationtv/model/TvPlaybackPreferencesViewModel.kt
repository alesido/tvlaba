package org.alsi.android.presentationtv.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class TvPlaybackPreferencesViewModel @Inject constructor() : ViewModel() {

    private val changeLiveData:
            MutableLiveData<Resource<PlaybackPreferenceChangeEvent>> = MutableLiveData()

    var trackLanguageSelection: LanguageTrackSelection = LanguageTrackSelection.empty()

    fun getPreferenceChangeLiveData() = changeLiveData

    fun onAspectRatioChanged(aspectRatio: VideoAspectRatio) {
        val event = PlaybackAspectRatioChanged(aspectRatio)
        changeLiveData.value = Resource.success(event)
    }

}