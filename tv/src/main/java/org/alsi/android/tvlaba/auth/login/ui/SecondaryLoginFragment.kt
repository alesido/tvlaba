package org.alsi.android.tvlaba.auth.login.ui

import android.os.Bundle
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
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.presentation.auth.login.model.LoginViewModel
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import java.util.*
import javax.inject.Inject

class SecondaryLoginFragment : GuidedStepSupportFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var loginViewModel : LoginViewModel
    private val progressBarManager = ProgressBarManager()

    private var savedPassword: String? = null

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
        getString(R.string.description_for_secondary_login),
        getString(R.string.app_version, requireActivity().packageManager.getPackageInfo(
            requireActivity().packageName, 0).versionName),
        ContextCompat.getDrawable(requireContext(), R.drawable.settings_icon_metal)
    )

    override fun onCreateActions(actions: MutableList<GuidedAction>,
                                 savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.addAll(listOf(
            buttonEdit(),
            inputPin(savedInstanceState),
            inputPass(savedInstanceState),
            checkBoxRememberMe(savedInstanceState),
            buttonSubmit()
        ))
        // restore shadowed password
        savedPassword = savedInstanceState?.getString(STATE_KEY_SHADOWED)
    }

    override fun onCreateButtonActions(actions: MutableList<GuidedAction>,
                                       savedInstanceState: Bundle?) {
        super.onCreateButtonActions(actions, savedInstanceState)
        actions.addAll(listOf(
            buttonClear(),
            radioButtonEnglish(),
            radioButtonRussian(),
        ))
    }

    override fun onStart() {
        super.onStart()
        loginViewModel.liveData.observe(this, this::handleLoginResult)

        loginViewModel.liveAccount.observe(this, this::handleLastAccountResult)
        loginViewModel.lastSessionAccount() // request account to fill inputs

        // hide password in case it's restored by the standard scheme
        // to which "onCreateActions" wrapped around
        findActionById(ID_PASS).description = null

        // looks like we have to refresh inputs as their enable status changed
        notifyInputsChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_KEY_EDIT_ENABLED, findActionById(ID_PIN).isEnabled)
        outState.putString(STATE_KEY_SHADOWED, findActionById(ID_PASS).description?.toString())
    }

    // endregion
    // region Main Fields  & Actions

    private fun buttonEdit() = GuidedAction.Builder(requireContext())
        .id(ID_BUTTON_UNLOCK_EDIT)
        .title(getString(R.string.button_label_unlock_edit).toUpperCase(Locale.getDefault()))
        .build()

    private fun inputPin(savedInstanceState: Bundle?) = GuidedAction.Builder(requireContext())
        .id(ID_PIN).title(getString(R.string.label_login_input_pin))
        .descriptionEditable(true)
        .descriptionInputType(InputType.TYPE_CLASS_TEXT)
        .enabled(savedInstanceState?.getBoolean(
            STATE_KEY_EDIT_ENABLED, false)?: false)
        .build()

    private fun inputPass(savedInstanceState: Bundle?) = GuidedAction.Builder(requireContext())
        .id(ID_PASS).title(getString(R.string.label_login_input_pass))
        .descriptionEditable(true)
        .descriptionInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD
                or InputType.TYPE_CLASS_TEXT)
        .enabled(savedInstanceState?.getBoolean(
            STATE_KEY_EDIT_ENABLED, false)?: false)
        .build()

    private fun checkBoxRememberMe(savedInstanceState: Bundle?) = GuidedAction.Builder(requireContext())
        .id(ID_REMEMBER_ME).title(getString(R.string.label_login_remember_me))
        .checkSetId(CHECKBOX_CHECK_SET_ID)
        .checked(false)
        .enabled(savedInstanceState?.getBoolean(
            STATE_KEY_EDIT_ENABLED, false)?: false)
        .build()

    private fun buttonSubmit() = GuidedAction.Builder(requireContext())
        .id(ID_BUTTON_SUBMIT)
        .title(getString(R.string.button_label_submit).toUpperCase(Locale.getDefault()))
        .build()

    // endregion
    // region Extra Actions

    private fun buttonClear() = GuidedAction.Builder(requireContext())
        .id(ID_BUTTON_CLEAR)
        .title(getString(R.string.button_label_clear).toUpperCase(Locale.getDefault()))
        .build()

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

    // endregion
    // region Interaction

    private fun handleLastAccountResult(resource: Resource<UserAccount>) {
        when (resource.status) {
            LOADING -> progressBarManager.show()
            SUCCESS -> {
                progressBarManager.hide()
                resource.data?.run {
                    if (preferences?.loginRememberMe == true) {
                        // do not overwrite pin field in case it has nonempty value
                        // (already set and then edited)
                        findActionById(ID_PIN).run {
                            if (description?.isEmpty() != false) {
                                description = loginName
                                notifyActionChanged(findActionPositionById(ID_PIN))
                            }
                        }
                        // just remember password, do not show it to reveal unintentionally
                        if (savedPassword?.isEmpty() != false) {
                            savedPassword = loginPassword
                        }
                        // set "remember me"
                        findActionById(ID_REMEMBER_ME).isChecked = true
                        notifyActionChanged(findActionPositionById(ID_REMEMBER_ME))
                    }
                    else {
                        unlockEditing()
                        findActionById(ID_BUTTON_UNLOCK_EDIT).isEnabled = false
                        notifyActionChanged(findActionPositionById(ID_BUTTON_UNLOCK_EDIT))
                    }
                }
            }
            ERROR -> {
                progressBarManager.hide()
                errorHandler.run(this, resource.throwable)
            }
        }
    }

    private fun handleLoginResult(resource: Resource<Unit>) {
        when (resource.status) {
            LOADING -> progressBarManager.show()
            SUCCESS -> {
                progressBarManager.hide()
                val nc = findNavController(this)
                nc.popBackStack()
                nc.navigate(R.id.actionGlobalOnLogIn)
            }
            ERROR -> {
                progressBarManager.hide()
                errorHandler.run(this, resource.throwable)
            }
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        when (action?.id) {
            ID_PIN -> onPassActionEntered() // exited PIN field with ENTER, and entered PASS field
            ID_BUTTON_CLEAR -> {
                savedPassword = null
                listOf(ID_PIN, ID_PASS).forEach {
                    findActionById(it)?.apply { description = ""; isEnabled = true }
                    notifyActionChanged(findActionPositionById(it))
                }
                findActionById(ID_REMEMBER_ME)?.apply { isEnabled = true }
                notifyActionChanged(findActionPositionById(ID_REMEMBER_ME))

            }
            ID_BUTTON_UNLOCK_EDIT -> unlockEditing()
            ID_BUTTON_SUBMIT -> {
                val pinInput = inputValue(ID_PIN)
                if (isInputEmpty(pinInput, R.string.message_login_pin_should_be_should_be_not_empty))
                    return
                if (isInputEmpty(savedPassword, R.string.message_login_pass_field_should_be_not_empty))
                    return
                loginViewModel.login(
                    pinInput!!,
                    savedPassword!!,
                    findActionById(ID_REMEMBER_ME).isChecked
                )
            }
            ID_LANG_EN -> changeLanguageTo("en")
            ID_LANG_RU -> changeLanguageTo("ru")
        }
    }

    override fun onGuidedActionFocused(currentFocused: GuidedAction?) {
        super.onGuidedActionFocused(currentFocused)
        if (currentFocused?.id == ID_PASS) { // focus received
            onPassActionEntered()
        }
    }

    private fun onPassActionEntered() {
        savedPassword?: return
        findActionById(ID_PASS).run {
            if (description?.isEmpty() != false) {
                description = savedPassword
                view?.post { // to avoid crash as the RecyclerView rather rebuilding layout at the moment
                    notifyActionChanged(findActionPositionById(ID_PASS))
                }
            }
        }
    }

    private fun unlockEditing() {
        listOf(ID_PIN, ID_PASS, ID_REMEMBER_ME).forEach {
            findActionById(it)?.apply { isEnabled = true }
            notifyActionChanged(findActionPositionById(it))
        }
        findActionById(ID_BUTTON_UNLOCK_EDIT).isEnabled = false
        notifyActionChanged(findActionPositionById(ID_BUTTON_UNLOCK_EDIT))
    }

    private fun notifyInputsChanged() {
        listOf(ID_PIN, ID_PASS, ID_REMEMBER_ME).forEach {
            notifyActionChanged(findActionPositionById(it))
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

        // action IDs

        const val ID_PIN = 1L
        const val ID_PASS = 2L
        const val ID_REMEMBER_ME = 3L

        const val ID_LANG_EN = 11L
        const val ID_LANG_RU = 12L

        const val ID_BUTTON_CLEAR = 21L
        const val ID_BUTTON_UNLOCK_EDIT = 22L
        const val ID_BUTTON_SUBMIT = 23L

        // state keys

        const val STATE_KEY_EDIT_ENABLED = "STATE_KEY_EDIT_ENABLED"
        const val STATE_KEY_SHADOWED = "STATE_KEY_SHADOWED"
    }
}