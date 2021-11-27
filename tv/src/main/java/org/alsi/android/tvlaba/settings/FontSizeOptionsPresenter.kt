package org.alsi.android.tvlaba.settings

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.alsi.android.domain.streaming.model.options.LanguageOption
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.presentation.state.ResourceState
import java.util.*

class FontSizeOptionsPresenter(
    preference: ListPreference,
    private val owner: PreferenceFragmentCompat
) {
    init {

        preference.value?: run { preference.value = "medium" }
        preference.summary = preference.entries[preference.findIndexOfValue(preference.value)]
        preference.onPreferenceChangeListener = Preference
            .OnPreferenceChangeListener(this::onPreferenceChange)
    }

    private fun onPreferenceChange(@Suppress("UNUSED_PARAMETER") preference: Preference,
                                   newValue: Any): Boolean {
        owner.requireActivity().recreate()
        return true
    }
}
