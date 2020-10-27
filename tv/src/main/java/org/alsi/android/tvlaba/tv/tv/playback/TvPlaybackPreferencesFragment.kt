package org.alsi.android.tvlaba.tv.tv.playback

import android.os.Bundle
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.lifecycle.ViewModelProviders
import androidx.preference.ListPreference
import androidx.preference.Preference
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.presentationtv.model.TvPlaybackViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject


class TvPlaybackPreferencesFragment : LeanbackPreferenceFragmentCompat() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var playbackViewModel: TvPlaybackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        playbackViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvPlaybackViewModel::class.java)
    }

    override fun onCreatePreferences(arguments: Bundle?, s: String?) {
        // inflate preferences screens
        addPreferencesFromResource(preferencesXmlRes)

        // aspect ratio
        val aspectRatioPreference = findPreference<ListPreference>("video_playback_option_aspect_ratio")
        aspectRatioPreference?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener {
            _, value -> playbackViewModel.onAspectRatioChanged(value); true
        }
    }

    companion object {
        private const val preferencesXmlRes = R.xml.video_playback_preferences
    }
}
