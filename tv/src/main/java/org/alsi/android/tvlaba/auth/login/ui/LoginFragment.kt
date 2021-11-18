package org.alsi.android.tvlaba.auth.login.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.leanback.widget.GuidedAction.CHECKBOX_CHECK_SET_ID
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.preference.PreferenceManager
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.presentation.auth.login.model.LoginViewModel
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import java.util.*
import javax.inject.Inject

class LoginFragment : GuidedStepSupportFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    private lateinit var loginViewModel : LoginViewModel

    private val progressBarManager = ProgressBarManager()

    // region Android

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(this, viewModelFactory)
            .get(LoginViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        // extend fragment view to full screen
        view.children.elementAt(0).layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        // progress bar
        setupProgressBar(view, inflater)
        return view
    }

    private fun setupProgressBar(view: ViewGroup, inflater: LayoutInflater) {
        val progressView = inflater.inflate(R.layout.progress_view_common, view, false)
        view.addView(progressView)
        progressBarManager.enableProgressBar()
        progressBarManager.setProgressBarView(progressView)
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?) = GuidanceStylist.Guidance(
        getString(R.string.app_name),
        getString(R.string.description_for_login),
        requireActivity().packageManager.getPackageInfo(
            requireActivity().packageName, 0).versionName,
        ContextCompat.getDrawable(requireContext(), R.drawable.settings_icon_metal)
    )

    override fun onCreateActions(actions: MutableList<GuidedAction>,
                                 savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.addAll(listOf(
            inputPin(),
            inputPass(),
            checkBoxRememberMe(),
            buttonSubmit()
        ))
    }

    override fun onCreateButtonActions(actions: MutableList<GuidedAction>,
                                       savedInstanceState: Bundle?) {
        super.onCreateButtonActions(actions, savedInstanceState)
        actions.addAll(listOf(
            radioButtonEnglish(),
            radioButtonRussian(),
            linkWiFi()
        ))
    }

    override fun onStart() {
        super.onStart()
        loginViewModel.liveData.observe(this, this::handleLoginResult)
    }

    // endregion
    // region Main Fields  & Actions

    private fun inputPin() = GuidedAction.Builder(requireContext())
        .id(ID_PIN).title(getString(R.string.label_login_input_pin))
        .descriptionEditable(true)
        .descriptionInputType(InputType.TYPE_CLASS_NUMBER)
        .build()

    private fun inputPass() = GuidedAction.Builder(requireContext())
        .id(ID_PASS).title(getString(R.string.label_login_input_pass))
        .descriptionEditable(true)
        .descriptionInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD
                or InputType.TYPE_CLASS_NUMBER)
        .build()

    private fun checkBoxRememberMe() = GuidedAction.Builder(requireContext())
        .id(ID_REMEMBER_ME).title(getString(R.string.label_login_remember_me))
        .checkSetId(CHECKBOX_CHECK_SET_ID)
        .checked(false)
        .build()

    private fun buttonSubmit() = GuidedAction.Builder(requireContext())
        .id(ID_BUTTON_SUBMIT)
        .title(getString(R.string.button_label_submit).toUpperCase(Locale.getDefault()))
        .build()

    // endregion
    // region Extra Actions

    private fun radioButtonEnglish() = GuidedAction.Builder(requireContext())
        .id(ID_LANG_EN).title(getString(R.string.label_language_english))
        .checkSetId(1)
        .checked(Locale.getDefault().language == "en")
        .build()

    private fun radioButtonRussian() = GuidedAction.Builder(requireContext())
        .id(ID_LANG_RU).title(getString(R.string.label_language_russian))
        .checkSetId(1)
        .checked(Locale.getDefault().language == "ru")
        .build()

    private fun linkWiFi() = GuidedAction.Builder(requireContext())
        .id(ID_WIFI).title(getString(R.string.label_wifi))
        .icon(R.drawable.settings_icon_metal)
        .build()

    // endregion
    // region Interaction

    private fun handleLoginResult(resource: Resource<Unit>) {

        when(resource.status) {
            LOADING -> {}
            SUCCESS -> {
                val nc = findNavController(this)
                nc.popBackStack()
                nc.navigate(R.id.actionGlobalOnLogIn)
            }
            ERROR -> showErrorMessage(resource.message)
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        when (action?.id) {
            ID_LANG_EN -> changeLanguageTo("en")
            ID_LANG_RU -> changeLanguageTo("ru")
            ID_WIFI -> {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent, null)
            }
            ID_BUTTON_SUBMIT -> {
                val pinInput = inputValue(ID_PIN)
                if (isInputEmpty(pinInput, R.string.message_all_parent_pins_should_be_filled))
                    return
                val passInput = inputValue(ID_PASS)
                if (isInputEmpty(passInput, R.string.message_all_parent_pins_should_be_filled))
                    return
                loginViewModel.login(
                    pinInput!!,
                    passInput!!,
                    findActionById(ID_REMEMBER_ME).isChecked
                )
            }
        }
    }

    // endregion
    // region Helpers

    private fun changeLanguageTo(newLanguageCode: String) {
        if (Locale.getDefault().language == newLanguageCode)
            return

        val newLocale = Locale(newLanguageCode)
        Locale.setDefault(newLocale)

        val configuration = resources.configuration
        configuration.setLocale(newLocale)
        requireContext().createConfigurationContext(resources.configuration)

        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
            .putString("pref_key_app_language", newLanguageCode).apply()

        requireActivity().recreate()
    }

    private fun inputValue(id: Long) = findActionById(id)?.description?.toString()

    private fun isInputEmpty(input: CharSequence?, messageRes: Int): Boolean {
        if  (input?.isEmpty() != false) {
            showErrorMessage(getString(messageRes))
            return true
        }
        return false
    }

    private fun showErrorMessage(message: String?) {
        val toast = Toast.makeText(
            requireContext(),
            message?: getString(R.string.exception_message_generic_error),
            Toast.LENGTH_SHORT
        )
        with(toast) {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lite_red))
            view.setPadding(24)
        }
        toast.show()
    }

    // endregion

    companion object {
        const val ID_PIN = 1L
        const val ID_PASS = 2L
        const val ID_REMEMBER_ME = 3L

        const val ID_LANG_EN = 11L
        const val ID_LANG_RU = 12L
        const val ID_WIFI = 13L

        const val ID_BUTTON_SUBMIT = 21L
    }

}