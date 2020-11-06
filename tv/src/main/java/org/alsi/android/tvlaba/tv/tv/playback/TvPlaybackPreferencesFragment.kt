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

        initAspectRatioPreference()
        initAudioTrackLanguagePreference()
        initTextTrackLanguagePreference()

        // subtitles languages
//        val textTrackLanguagePreference = findPreference<ListPreference>("video_playback_option_subtitles_language")
//        textTrackLanguagePreference?.let { preference ->
//            // availability
//            val tracks = preferencesViewModel.trackLanguageSelection?.textTracks
//            if (tracks == null || tracks.isEmpty()) {
//                preference.summary = getString(R.string.preference_summary_not_available)
//            }
//            else {
//                // entries and initial selection
//                preference.entries = Array<CharSequence>(tracks.size + 1) { i ->
//                    if (i > 0) tracks[i - 1] else getString(R.string.preference_subtitles_is_off)
//                }
//                preference.entryValues = Array<CharSequence>(tracks.size + 1) { i -> i.toString() }
//                val selectedTextTrackIndex = preferencesViewModel.trackLanguageSelection?.selectedTextTrackIndex
//                if (selectedTextTrackIndex != null) {
//                    preference.setDefaultValue((selectedTextTrackIndex + 1).toString())
//                    preference.value = (selectedTextTrackIndex + 1).toString()
//                    preference.summary = tracks[selectedTextTrackIndex]
//                }
//                else {
//                    preference.setDefaultValue("0")
//                    preference.value = "0"
//                    preference.summary = getString(R.string.preference_subtitles_is_off)
//
//                }
//            }
//            preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {
//                _, selectedValue ->
//                val selectedIndex = selectedValue.toString().toInt()
//                if (selectedIndex > 0) {
//                    preferencesViewModel.onTextTrackLanguageSelected(selectedIndex - 1)
//                    preference.summary = preference.entries[selectedIndex]
//                }
//                else {
//                    preferencesViewModel.turnSubtitlesOff()
//                    preference.summary = getString(R.string.preference_subtitles_is_off)
//                }
//                true
//            }
//        }
    }

    private fun initAspectRatioPreference() {
        val preference = findPreference<ListPreference>(
                "video_playback_option_aspect_ratio")?: return

        // entries and values (entries are set from an array resource)
        preference.entryValues = Array<CharSequence>(preference.entries.size) { i -> i.toString() }

        // default value
        val defaultEntry = getString(R.string.video_aspect_16_9)
        val defaultEntryValue = preference.entries.indexOf(defaultEntry).toString()
        preference.setDefaultValue(preference.entries.indexOf(defaultEntry))

        // initially selected value (the same as default for now)
        preference.value = defaultEntryValue
        preference.summary = defaultEntry

        // change listening
        preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {
            _, selectedValue ->
            val selectedIndex = selectedValue.toString().toInt()
            if (selectedIndex >= 0 && selectedIndex < VideoAspectRatio.values().size) {
                preferencesViewModel.onAspectRatioChanged(VideoAspectRatio.values()[selectedIndex])
                preference.summary = preference.entries[selectedIndex]
            }
            true
        }
    }

    private fun initAudioTrackLanguagePreference() {
        val preference = findPreference<ListPreference>(
                "video_playback_option_audio_language")?: return

        val tracks = preferencesViewModel.trackLanguageSelection?.audioTracks

        if (tracks == null || tracks.isEmpty()) {
            // no options
            preference.isEnabled = false
        }
        else {
            // entries and values
            preference.entries = Array<CharSequence>(tracks.size) { i -> tracks[i] }
            preference.entryValues = Array<CharSequence>(tracks.size) { i -> i.toString() }

            // default value
            preference.setDefaultValue("0")

            // initial selection
            val selectedAudioTrackIndex = preferencesViewModel.trackLanguageSelection?.selectedAudioTrackIndex?: 0
            preference.value = selectedAudioTrackIndex.toString()
            preference.summary = tracks[selectedAudioTrackIndex]
        }

        // change listening
        preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {
            _, selectedValue ->
            val selectedIndex = selectedValue.toString().toInt()
            preferencesViewModel.onAudioTrackLanguageSelected(selectedIndex)
            preference.summary = preference.entries[selectedIndex]
            true
        }
    }

    private fun initTextTrackLanguagePreference() {
        val preference = findPreference<ListPreference>(
                "video_playback_option_subtitles_language")?: return

        val tracks = preferencesViewModel.trackLanguageSelection?.textTracks
        if (tracks == null || tracks.isEmpty()) {
            // no options
            preference.isEnabled = false
        }
        else {
            // entries and values
            preference.entries = Array<CharSequence>(tracks.size + 1) { i ->
                if (i > 0) tracks[i - 1] else getString(R.string.preference_subtitles_is_off)
            }
            preference.entryValues = Array<CharSequence>(tracks.size + 1) { i -> i.toString() }

            // initial selection and default value
            val selectedTextTrackIndex = preferencesViewModel.trackLanguageSelection?.selectedTextTrackIndex
            if (selectedTextTrackIndex != null) {
                preference.setDefaultValue((selectedTextTrackIndex + 1).toString())
                preference.value = (selectedTextTrackIndex + 1).toString()
                preference.summary = tracks[selectedTextTrackIndex]
            }
            else {
                // set subtitles is off
                preference.setDefaultValue("0")
                preference.value = "0"
                preference.summary = getString(R.string.preference_subtitles_is_off)
            }

            // change listening
            preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {
                _, selectedValue ->
                val selectedIndex = selectedValue.toString().toInt()
                if (selectedIndex > 0) {
                    preferencesViewModel.onTextTrackLanguageSelected(selectedIndex - 1)
                    preference.summary = preference.entries[selectedIndex]
                }
                else {
                    preferencesViewModel.turnSubtitlesOff()
                    preference.summary = getString(R.string.preference_subtitles_is_off)
                }
                true
            }
        }
    }

    companion object {
        private const val preferencesXmlRes = R.xml.video_playback_preferences
    }
}
