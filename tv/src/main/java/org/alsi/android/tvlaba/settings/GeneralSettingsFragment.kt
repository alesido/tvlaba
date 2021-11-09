package org.alsi.android.tvlaba.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.presentation.settings.GeneralSettingsEventKind
import org.alsi.android.presentation.settings.GeneralSettingsEventKind.LANGUAGE_CHANGED
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.settings.parental.ParentalControlPinFragment
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject

class GeneralSettingsFragment : LeanbackPreferenceFragmentCompat() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var viewModel: GeneralSettingsViewModel

    private lateinit var streaming: StreamingSettingsPresenter
    private lateinit var language: LanguageOptionsPresenter

    private val progressBarManager = ProgressBarManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)
            .get(GeneralSettingsViewModel::class.java)

        errorHandler.changeContext(requireActivity())

        setupPreferences()
    }

    private fun setupPreferences() {
        // language
        findPreference<ListPreference>(getString(R.string.pref_key_app_language))?.let {
            language = LanguageOptionsPresenter(it, owner = this, viewModel)
        }

        // streaming
        findPreference<PreferenceCategory>(getString(R.string.pref_key_streaming_category))?.let {
            streaming = StreamingSettingsPresenter(it, owner = this, viewModel)
        }

        // parental control
        findPreference<Preference>(getString(R.string.pref_key_parental_code))
            ?.setOnPreferenceClickListener { onParentalCodeClicked(); true }

        // exit
        findPreference<Preference>(getString(R.string.pref_key_exit))
            ?.setOnPreferenceClickListener {
                findNavController(this).navigate(R.id.actionGlobalLogOut); true
            }
    }

    private fun onParentalCodeClicked() {
        // close dialog wrapping this leanback settings fragment
        (parentFragment?.parentFragment as DialogFragment).dismiss()

        // add guided step fragment view (for parental code editing dialog)
        // to the views hierarchy
        openParentalControlSetupGuide()
    }

    private fun onExitClicked() {
        findNavController(this).navigate(R.id.actionGlobalLogOut)
    }

    /**
     *  Simplified replacement for GuidedStepSupportFragment transactions to add and replace. Also,
     *  the back stack support is off.
     *
     *  The reason is that it used here in single-activity and jetpack navigation context, not in a
     *  separate activity environment for which it considered initially (and demoed in the leanback
     *  sample application).
     *
     *  The other reason is that standard approach was incorrect managing the back stack -
     *  step guide fragment was not actually removed while its view was destroyed.
     */
    private fun openParentalControlSetupGuide() {
        val fragment = ParentalControlPinFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        fragment.uiStyle = GuidedStepSupportFragment.UI_STYLE_ENTRANCE
        transaction
            .replace(android.R.id.content, fragment, fragment.javaClass.toString())
            .commit()
    }

    override fun onCreatePreferences(arguments: Bundle?, s: String?) {
        addPreferencesFromResource(preferencesXmlRes)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getLiveSettingValues().observe(this) { if (it != null) handleLiveData(it) }
        viewModel.getEventChannel().observe(this) { event ->
            event?.contentIfNotHandled?.let { handleEvent(it) }
        }
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

    private fun handleEvent(eventKind: GeneralSettingsEventKind) {
        if (eventKind == LANGUAGE_CHANGED) {
            (parentFragment?.parentFragment as DialogFragment).dismiss()
            requireActivity().recreate()
        }
    }

    companion object {
        private const val preferencesXmlRes = R.xml.general_settings
    }
}