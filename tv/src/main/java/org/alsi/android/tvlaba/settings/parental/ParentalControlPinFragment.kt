package org.alsi.android.tvlaba.settings.parental

import android.os.Bundle
import android.text.InputType.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.setPadding
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.app.ProgressBarManager
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.presentation.settings.ParentalControlViewModel
import org.alsi.android.presentation.state.ResourceState.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject


class ParentalControlPinFragment : GuidedStepSupportFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var viewModel: ParentalControlViewModel

    private val progressBarManager = ProgressBarManager()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        // extend fragment view to full screen
        view.children.elementAt(0).layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        // avoid finishing this single-activity app due to default back press behavior
        addBackPressedCallback()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)
            .get(ParentalControlViewModel::class.java)

        errorHandler.changeContext(requireActivity())
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?) = GuidanceStylist.Guidance(
        "Parent PIN",
    "Change the PIN to secure your parent control",
        "",
        ContextCompat.getDrawable(requireContext(), R.drawable.settings_icon_metal)
    )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.add(GuidedAction.Builder(requireContext())
            .id(ID_CURRENT_PIN).title("Current PIN").descriptionEditable(true)
            .descriptionInputType(TYPE_TEXT_VARIATION_PASSWORD or TYPE_CLASS_TEXT)
            .build())
        actions.add(GuidedAction.Builder(requireContext())
            .id(ID_NEW_PIN_1).title("New PIN").descriptionEditable(true)
            .descriptionInputType(TYPE_TEXT_VARIATION_PASSWORD or TYPE_CLASS_TEXT)
            .build())
        actions.add(GuidedAction.Builder(requireContext())
            .id(ID_NEW_PIN_2).title("Repeat PIN to validate your input").descriptionEditable(true)
            .descriptionInputType(TYPE_TEXT_VARIATION_PASSWORD or TYPE_CLASS_TEXT)
            .build())
        actions.add(GuidedAction.Builder(requireContext())
            .id(ID_BUTTON_SUBMIT).title("SUBMIT")
            .build())
    }

    override fun onStart() {
        super.onStart()
        viewModel.getLiveData().observe(this) { resource ->
            when(resource.status) {
                LOADING -> progressBarManager.show()
                SUCCESS -> {
                    progressBarManager.hide()
                    dismiss()
                }
                ERROR -> {
                    progressBarManager.hide()
                    errorHandler.run(this, resource.throwable)
                }
            }
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        if (action?.id == ID_BUTTON_SUBMIT) {
            val currentPinInput = description(ID_CURRENT_PIN)
            if (isInputEmpty(currentPinInput, "Please provide your current PIN.")) return
            val newPinInput = description(ID_NEW_PIN_1)
            if (isInputEmpty(newPinInput, "Please provide your current PIN.")) return
            val newPinConfirmation = description(ID_NEW_PIN_2)
            if (isInputEmpty(newPinConfirmation,
                    "Please repeat new PIN input to verify it's correct.")) return
            if  (newPinInput != newPinConfirmation) {
                showValidationMessage("New PIN input and New PIN Confirmation input do not match!")
                return
            }
            viewModel.changeParentalControlPin(currentPinInput!!, newPinInput!!)
        }
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

    private fun addBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object:
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    remove() // remove this listener
                    dismiss() // dismiss this fragment
                }
            }
        )
    }

    private fun dismiss() {
        requireActivity().supportFragmentManager.beginTransaction()
            .remove(this@ParentalControlPinFragment)
            .commit()
    }

    companion object {
        const val ID_CURRENT_PIN = 1L
        const val ID_NEW_PIN_1 = 2L
        const val ID_NEW_PIN_2 = 3L
        const val ID_BUTTON_SUBMIT = 10L
    }
}