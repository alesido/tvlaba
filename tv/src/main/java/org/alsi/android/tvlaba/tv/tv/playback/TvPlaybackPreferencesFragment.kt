@file:Suppress("DEPRECATION")

package org.alsi.android.tvlaba.tv.tv.playback

import android.os.Bundle
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.leanback.preference.LeanbackSettingsFragmentCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import org.alsi.android.tvlaba.R

class TvPlaybackPreferencesFragment : LeanbackSettingsFragmentCompat()
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
        return PrefFragment()
    }

    class PrefFragment : LeanbackPreferenceFragmentCompat() {
        override fun onCreatePreferences(arguments: Bundle?, s: String?) {
            addPreferencesFromResource(preferencesXmlRes)
        }
    }

    companion object {
        private const val preferencesXmlRes = R.xml.video_playback_preferences
    }
}