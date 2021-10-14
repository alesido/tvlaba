package org.alsi.android.tvlaba.settings

import androidx.annotation.StringRes
import androidx.preference.ListPreference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.tvlaba.R

class StreamingSettingsPresenter(
    private val root: PreferenceCategory,
    private val owner: PreferenceFragmentCompat,
    private val viewModel: GeneralSettingsViewModel
) {
    init {
        // server options
        initListPreference(R.string.pref_key_streaming_server) {
            ServerSettingPresenter(it, owner, viewModel)
        }
        // cache size options
        initListPreference(R.string.pref_key_server_stream_cache_size) {
            StreamCacheSizeOptionsPresenter(it, owner, viewModel)
        }
    }

    private fun initListPreference(@StringRes keyRes: Int, setup: (lp: ListPreference) -> Unit) {
        root.findPreference<ListPreference>(owner.getString(keyRes))?.let { setup(it) }
    }
}
