package org.alsi.android.tvlaba.tv.tv.playback

import android.os.Bundle
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.lifecycle.ViewModelProviders
import androidx.preference.ListPreference
import androidx.preference.Preference
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio
import org.alsi.android.presentationtv.model.TvPlaybackPreferencesViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject


class TvPlaybackPreferencesFragment : LeanbackPreferenceFragmentCompat() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var preferencesViewModel: TvPlaybackPreferencesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(arguments: Bundle?, s: String?) {

        preferencesViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(TvPlaybackPreferencesViewModel::class.java)

        // inflate preferences screens
        addPreferencesFromResource(preferencesXmlRes)

        // aspect ratio
        val aspectRatioPreference = findPreference<ListPreference>("video_playback_option_aspect_ratio")
        aspectRatioPreference?.let { it.summary = it.entries[4] }
        aspectRatioPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {
            preference, selectedValue ->
            val selectedIndex = (preference as ListPreference).entries.indexOf(selectedValue)
            if (selectedIndex >= 0 && selectedIndex < VideoAspectRatio.values().size) {
                preferencesViewModel.onAspectRatioChanged(VideoAspectRatio.values()[selectedIndex])
                preference.summary = preference.entries[selectedIndex]
            }
            true
        }

        // audio track languages
        val audioTrackLanguagePreference = findPreference<ListPreference>("video_playback_option_audio_language")
        audioTrackLanguagePreference?.let { preference ->
            val tracks = preferencesViewModel.currentTrackSelection.audioTracks
            if (tracks.isEmpty()) {
                preference.summary = "N/A"
            }
            else {
                preference.entries = Array<CharSequence>(tracks.size) { i -> tracks[i] }
                preference.entryValues = Array<CharSequence>(tracks.size) { i -> tracks[i] }
                preference.setDefaultValue(tracks[0])
                preference.summary = tracks[0]
            }
        }
    }

    companion object {
        private const val preferencesXmlRes = R.xml.video_playback_preferences
    }
}
