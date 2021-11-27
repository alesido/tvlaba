package org.alsi.android.tvlaba.settings

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.presentation.state.ResourceState
import java.util.*

class LanguageOptionsPresenter(
    preference: ListPreference,
    private val owner: PreferenceFragmentCompat,
    private val viewModel: GeneralSettingsViewModel
) {
    private var last: LanguageOption? = null

    init {
        viewModel.getLiveSettingValues().observe(owner) { resource ->
            if (resource.status == ResourceState.SUCCESS) { // to restore data on error
                resource.data?.language?.let {
                    preference.setDefaultValue(it.code)
                    preference.value = it.code
                    preference.summary = it.name
                    if (last != null)
                        updateLanguageTo(it.code)
                    last = it
                }?: run { preference.isEnabled = false }
            }
            if (resource.status == ResourceState.ERROR) {
                last?.let {
                    preference.setDefaultValue(it.code)
                    preference.value = it.code
                    preference.summary = it.name
                }
            }
        }
        
        viewModel.getLiveSettingsProfile().observe(owner) { resource ->
            if (resource.status == ResourceState.SUCCESS) { // to restore data on error
                val languages = resource.data?.languages
                if (null == languages || languages.size < 2) {
                    preference.isEnabled = false
                }
                else {
                    preference.isEnabled = true
                    preference.entries = languages.map { it.name }.toTypedArray()
                    preference.entryValues = languages.map { it.code }.toTypedArray()
                    preference.onPreferenceChangeListener = Preference
                        .OnPreferenceChangeListener(this::onPreferenceChange)
                }
            }
        }
    }

    private fun onPreferenceChange(@Suppress("UNUSED_PARAMETER") preference: Preference,
                                   newValue: Any): Boolean {
        viewModel.selectLanguage(newValue as String)
        return true
    }

    private fun updateLanguageTo(newLanguageCode: String) {
        if (Locale.getDefault().language == newLanguageCode)
            return

        val newLocale = Locale(newLanguageCode)
        Locale.setDefault(newLocale)

        val configuration = owner.resources.configuration
        configuration.setLocale(newLocale)
        owner.requireContext().createConfigurationContext(owner.resources.configuration)

        // NOTE Activity recreated later, on the successful server request
    }
}
