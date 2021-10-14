package org.alsi.android.tvlaba.settings

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.alsi.android.domain.streaming.model.options.DeviceModelOption
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.presentation.state.ResourceState

class DeviceModelOptionsPresenter(
    preference: ListPreference,
    owner: PreferenceFragmentCompat,
    private val viewModel: GeneralSettingsViewModel
) {
    private var last: DeviceModelOption? = null

    init {
        viewModel.getLiveSettingValues().observe(owner) { resource ->
            if (resource.status == ResourceState.SUCCESS) { // to restore data on error
                resource.data?.device?.let {
                    last = it
                    preference.setDefaultValue(it.id.toString())
                    preference.value = it.id.toString()
                    preference.summary = it.name
                }?: run { preference.isEnabled = false }
            }
            if (resource.status == ResourceState.ERROR) {
                last?.let {
                    preference.setDefaultValue(it.id.toString())
                    preference.value = it.id.toString()
                    preference.summary = it.name
                }
            }
        }
        
        viewModel.getLiveSettingsProfile().observe(owner) { resource ->
            if (resource.status == ResourceState.SUCCESS) { // to restore data on error
                val devices = resource.data?.devices
                if (null == devices || devices.size < 2) {
                    preference.isEnabled = false
                }
                else {
                    preference.isEnabled = true
                    preference.entries = devices.map { it.name }.toTypedArray()
                    preference.entryValues = devices.map { it.id.toString() }.toTypedArray()
                    preference.onPreferenceChangeListener = Preference
                        .OnPreferenceChangeListener(this::onPreferenceChange)
                }
            }
        }
    }

    private fun onPreferenceChange(@Suppress("UNUSED_PARAMETER") preference: Preference,
                                   newValue: Any): Boolean {
        viewModel.selectDeviceModel((newValue as String).toLong())
        return true
    }
}
