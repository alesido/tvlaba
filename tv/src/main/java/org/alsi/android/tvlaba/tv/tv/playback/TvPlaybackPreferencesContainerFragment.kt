package org.alsi.android.tvlaba.tv.tv.playback

import androidx.leanback.preference.LeanbackSettingsFragmentCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen

@Suppress("unused") // actually referenced (used) in a layout markup
class TvPlaybackPreferencesContainerFragment : LeanbackSettingsFragmentCompat()
{
    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(buildPreferenceFragment())
    }

    override fun onPreferenceStartFragment(
            preferenceFragment: PreferenceFragmentCompat?,
            preference: Preference?) = false

    override fun onPreferenceStartScreen(
            preferenceFragment: PreferenceFragmentCompat?,
            preferenceScreen: PreferenceScreen): Boolean {
        startPreferenceFragment(buildPreferenceFragment())
        return true
    }

    private fun buildPreferenceFragment(): PreferenceFragmentCompat {
        return TvPlaybackPreferencesFragment()
    }
}