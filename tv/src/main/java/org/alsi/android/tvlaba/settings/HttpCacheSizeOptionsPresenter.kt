package org.alsi.android.tvlaba.settings

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.presentation.state.ResourceState

class HttpCacheSizeOptionsPresenter(
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
                }?: run { preference.isEnabled = false }
            }
        }
        
        viewModel.getLiveSettingsProfile().observe(owner) { resource ->
            if (resource.status == ResourceState.SUCCESS) {
                val cacheSizes = resource.data?.cacheSizes
                if (null == cacheSizes || cacheSizes.size < 2) {
                    preference.isEnabled = false
                }
                else {
                    preference.isEnabled = true
                    preference.entries = cacheSizes.map { it.toString() }.toTypedArray()
                    preference.entryValues = preference.entries
                    preference.onPreferenceChangeListener = Preference
                        .OnPreferenceChangeListener(this::onPreferenceChange)
                }
            }
        }
    }

    private fun onPreferenceChange(@Suppress("UNUSED_PARAMETER") preference: Preference,
                                   newValue: Any): Boolean {
        viewModel.selectCacheSize((newValue as String).toLong())
        return true
    }
}
