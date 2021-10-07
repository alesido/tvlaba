package org.alsi.android.tvlaba.settings

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.presentation.state.ResourceState

class ServerSettingPresenter(
    preference: ListPreference,
    owner: PreferenceFragmentCompat,
    private val viewModel: GeneralSettingsViewModel
) {
    init {
        viewModel.getLiveSettingValues().observe(owner, { resource ->
            if (resource.status == ResourceState.SUCCESS) {
                resource.data?.server?.apply {
                    preference.setDefaultValue(tag)
                    preference.value = tag
                    preference.summary = title
                }
            }
        })

        viewModel.getLiveSettingsProfile().observe(owner, { resource ->
            if (resource.status == ResourceState.SUCCESS) {
                resource.data?.let { profile ->
                    preference.isEnabled = true
                    preference.entries = profile.servers.map { it.title }.toTypedArray()
                    preference.entryValues = preference.entries
                    preference.onPreferenceChangeListener = Preference
                        .OnPreferenceChangeListener(this::onPreferenceChange)
                }?: let {
                    preference.isEnabled = false
                }
            }
        })
    }

    private fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        viewModel.selectStreamingServer(newValue as String)
        return true
    }
}
