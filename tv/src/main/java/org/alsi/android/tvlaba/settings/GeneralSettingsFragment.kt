package org.alsi.android.tvlaba.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceCategory
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject

class GeneralSettingsFragment : LeanbackPreferenceFragmentCompat() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var viewModel: GeneralSettingsViewModel

    private lateinit var streaming: StreamingSettingsPresenter

    private val progressBarManager = ProgressBarManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)
            .get(GeneralSettingsViewModel::class.java)

        setupPreferences()
    }

    private fun setupPreferences() {
        // language

        // streaming
        findPreference<PreferenceCategory>(getString(R.string.pref_key_streaming_category))?.let {
            streaming = StreamingSettingsPresenter(it, owner = this, viewModel)
        }

        // parental control
    }

    override fun onCreatePreferences(arguments: Bundle?, s: String?) {
        addPreferencesFromResource(preferencesXmlRes)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getLiveSettingValues().observe(this, {
            if (it != null) handleLiveData(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        val progressView = inflater.inflate(R.layout.progress_view_common, view, false)
        view.addView(progressView)
        progressBarManager.enableProgressBar()
        progressBarManager.setProgressBarView(progressView)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.dispose()
    }

    private fun handleLiveData(resource: Resource<Any>) {
        when (resource.status) {
            ResourceState.LOADING -> progressBarManager.show( )
            ResourceState.SUCCESS -> progressBarManager.hide()
            ResourceState.ERROR -> {
                progressBarManager.hide()
                errorHandler.run(this, resource.throwable)
            }
        }
    }

    companion object {
        private const val preferencesXmlRes = R.xml.general_settings
    }
}