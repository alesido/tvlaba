package org.alsi.android.tvlaba.settings

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.presentation.state.ResourceState

class StreamBitrateOptionsPresenter(
    preference: ListPreference,
    owner: PreferenceFragmentCompat,
    private val viewModel: GeneralSettingsViewModel
) {
    init {
        viewModel.getLiveSettingValues().observe(owner) { resource ->
            if (resource.status == ResourceState.SUCCESS) {
                resource.data?.bitrate?.let {
                    preference.setDefaultValue(it.value.toString())
                    preference.value = it.value.toString()
                    preference.summary = it.title
                }?: run { preference.isEnabled = false }
            }
        }
        
        viewModel.getLiveSettingsProfile().observe(owner) { resource ->
            if (resource.status == ResourceState.SUCCESS) {
                val bitrates = resource.data?.bitrates
                if (null == bitrates || bitrates.size < 2) {
                    preference.isEnabled = false
                }
                else {
                    preference.isEnabled = true
                    preference.entries = bitrates.map { it.title }.toTypedArray()
                    preference.entryValues = bitrates.map { it.value.toString() }.toTypedArray()
                    preference.onPreferenceChangeListener = Preference
                        .OnPreferenceChangeListener(this::onPreferenceChange)
                }
            }
        }
    }

    private fun onPreferenceChange(@Suppress("UNUSED_PARAMETER") preference: Preference,
                                   newValue: Any): Boolean {
        viewModel.selectStreamBitrate((newValue as String).toInt())
        return true
    }
}
