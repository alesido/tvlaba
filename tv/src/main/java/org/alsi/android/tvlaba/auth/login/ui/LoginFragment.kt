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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.presentation.auth.login.model.LoginViewModel
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import java.util.*
import javax.inject.Inject

class LoginFragment : GuidedStepSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var loginViewModel : LoginViewModel

    private val progressBarManager = ProgressBarManager()

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

    override fun onCreateGuidance(savedInstanceState: Bundle?) = GuidanceStylist.Guidance(
        getString(R.string.app_name),
        getString(R.string.description_for_login),
        "",
        ContextCompat.getDrawable(requireContext(), R.drawable.settings_icon_metal)
    )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.add(
            GuidedAction.Builder(requireContext())
            .id(ID_LANG_EN).title("EN")
            .checkSetId(1).checked(true)
            .build())
        actions.add(
            GuidedAction.Builder(requireContext())
            .id(ID_LANG_RU).title("RU")
            .checkSetId(1).checked(false)
            .build())
        actions.add(
            GuidedAction.Builder(requireContext())
            .id(ID_PIN).title(getString(R.string.label_login_input_pin))
            .description("3848")
            .descriptionEditable(true)
            .descriptionInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT)
            .build())
        actions.add(
            GuidedAction.Builder(requireContext())
            .id(ID_PASS).title(getString(R.string.label_login_input_pass))
            .description("1234567890")
            .descriptionEditable(true)
            .descriptionInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT)
            .build())
        actions.add(
            GuidedAction.Builder(requireContext())
            .id(11L).title("WiFi")
            .icon(R.drawable.settings_icon_metal)
            .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
            .id(ID_BUTTON_SUBMIT).title(getString(R.string.button_label_submit)
                .toUpperCase(Locale.getDefault()))
            .build())
    }

    override fun onStart() {
        super.onStart()
        loginViewModel.liveData.observe(this, this::handleLoginResult)
    }

    private fun handleLoginResult(resource: Resource<Unit>) {

        when(resource.status) {
            LOADING -> {}
            SUCCESS -> {
                val nc = findNavController(this)
                nc.popBackStack()
                nc.navigate(R.id.actionGlobalOnLogIn)
            }
            ERROR -> Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        if (action?.id == ID_BUTTON_SUBMIT) {
        }
        if (action?.id == 11L) {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent, null)
        }
    }

    private fun setupProgressBar(view: ViewGroup, inflater: LayoutInflater) {
        val progressView = inflater.inflate(R.layout.progress_view_common, view, false)
        view.addView(progressView)
        progressBarManager.enableProgressBar()
        progressBarManager.setProgressBarView(progressView)
    }

    private fun description(id: Long) = findActionById(id)?.description?.toString()

    private fun isInputEmpty(input: CharSequence?, message: String): Boolean {
        if  (null == input || input.isEmpty()) {
            showValidationMessage(message)
            return true
        }
        return false
    }

    private fun showValidationMessage(message: String) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        with(toast) {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lite_red))
            view.setPadding(24)
        }
        toast.show()
    }

    companion object {
        const val ID_PIN = 1L
        const val ID_PASS = 2L
        const val ID_LANG_EN = 3L
        const val ID_LANG_RU = 4L
        const val ID_BUTTON_SUBMIT = 10L
    }

}