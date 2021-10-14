package org.alsi.android.tvlaba.settings

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.presentation.state.ResourceState

class StreamCacheSizeOptionsPresenter(
    preference: ListPreference,
    owner: PreferenceFragmentCompat,
    private val viewModel: GeneralSettingsViewModel
) {
    init {
        viewModel.getLiveSettingValues().observe(owner) { resource ->
            if (resource.status == ResourceState.SUCCESS) {
                resource.data?.cacheSize?.let {
                    preference.setDefaultValue(it)
                    preference.value = it.toString()
                    preference.summary = it.toString()
                }
            }
        }
        
        viewModel.getLiveSettingsProfile().observe(owner) { resource ->
            if (resource.status == ResourceState.SUCCESS) {
                resource.data?.let { profile ->
                    preference.isEnabled = true
                    preference.entries = profile.cacheSizes?.map { it.toString() }?.toTypedArray()
                    preference.entryValues = preference.entries
                    preference.onPreferenceChangeListener = Preference
                        .OnPreferenceChangeListener(this::onPreferenceChange)
                }?: let {
                    preference.isEnabled = false
                }
            }
        }
    }

    private fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        viewModel.selectCacheSize((newValue as String).toLong())
        return true
    }
}
