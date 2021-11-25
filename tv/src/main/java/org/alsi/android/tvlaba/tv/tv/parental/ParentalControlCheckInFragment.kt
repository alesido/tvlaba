package org.alsi.android.tvlaba.tv.tv.parental

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.alsi.android.presentation.settings.ParentalControlViewModel.ParentalServiceEventKind.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import java.util.*
import javax.inject.Inject

class ParentalControlCheckInFragment : GuidedStepSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var viewModel: ParentalControlViewModel

    private val progressBarManager = ProgressBarManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(), // shared view model
            viewModelFactory
        ).get(ParentalControlViewModel::class.java)

        errorHandler.changeContext(requireActivity())
    }

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

    override fun onCreateGuidance(savedInstanceState: Bundle?) = GuidanceStylist.Guidance(
        getString(R.string.title_enter_parent_pin),
        getString(R.string.description_why_to_enter_parent_pin),
        "",
        ContextCompat.getDrawable(requireContext(), R.drawable.settings_icon_metal)
    )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        super.onCreateActions(actions, savedInstanceState)
        actions.add(GuidedAction.Builder(requireContext())
            .id(ID_INPUT_PIN).title(getString(R.string.label_parent_pin))
            .descriptionEditable(true)
            .descriptionInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT)
            .build())
        actions.add(GuidedAction.Builder(requireContext())
            .id(ID_BUTTON_SUBMIT).title(getString(R.string.button_label_submit)
                .toUpperCase(Locale.getDefault()))
            .build())
    }

    override fun onStart() {
        super.onStart()
        viewModel.getServiceEventChannel().observe(this) { event ->
            event?.contentIfNotHandled?.let { eventCode ->
                when(eventCode) {
                    LOADING -> progressBarManager.show()
                    REQUEST_SUCCESS -> { progressBarManager.hide(); dismiss() }
                    ERROR -> { progressBarManager.hide(); errorHandler.run(this, event.error) }
                    else -> {}
                }
            }
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction?) {
        if (action?.id == ID_BUTTON_SUBMIT) {
            val input = findActionById(ID_INPUT_PIN)?.description?.toString()
            if (! isInputEmpty(input, getString(R.string.message_all_parent_pins_should_be_filled)))
                viewModel.authorizeAccessWith(input!!)
        }
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
            .remove(this@ParentalControlCheckInFragment)
            .commit()
    }

    private fun setupProgressBar(view: ViewGroup, inflater: LayoutInflater) {
        val progressView = inflater.inflate(R.layout.progress_view_common, view, false)
        view.addView(progressView)
        progressBarManager.enableProgressBar()
        progressBarManager.setProgressBarView(progressView)
    }

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
        const val ID_INPUT_PIN = 1L
        const val ID_BUTTON_SUBMIT = 10L
    }

}