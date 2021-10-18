package org.alsi.android.tvlaba.settings.parental0

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import org.alsi.android.presentation.settings.GeneralSettingsViewModel
import org.alsi.android.tvlaba.databinding.ParentalCodeDialogBinding
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.framework.SoftKeyboard
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject

class ParentalCodeDialogFragment : DialogFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var viewModel: GeneralSettingsViewModel


    private var _vb: ParentalCodeDialogBinding? = null
    private val vb get() = _vb!!

    private var softKeyboard: SoftKeyboard? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        errorHandler.changeContext(requireActivity())

        softKeyboard = SoftKeyboard(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        _vb = ParentalCodeDialogBinding.inflate(inflater, container, false)
        vb.submit.setOnClickListener { submit() }
        return vb.root
    }

    override fun onResume() {
        super.onResume()
        vb.newCodeEdit.post {
            vb.newCodeEdit.requestFocus()
            softKeyboard?.hide(vb.oldCodeEdit)
        }
    }

    private fun submit() {
//        val context = vb.newCodeEdit.context ?: return
//        val newCodeText = vb.newCodeEdit.text.toString()
//        val newCodeConfirmText = vb.newCodeConfirmEdit!!.text.toString()
//        if (newCodeText.isEmpty() && newCodeConfirmText.isEmpty()) {
//            Toast.makeText(
//                context,
//                R.string.parental_code_dialog_codes_empty,
//                Toast.LENGTH_SHORT
//            ).show()
//            return
//        }
//        if (newCodeText != newCodeConfirmText) {
//            Toast.makeText(
//                context,
//                R.string.parental_code_dialog_codes_not_match,
//                Toast.LENGTH_SHORT
//            ).show()
//        } else {
//            App.getComponent().getTvDataSource().changeParentCode(
//                vb.oldCodeEdit.text.toString(), vb.newCodeEdit.text.toString(), { dismiss() }
//            ) { error ->
//                val errorMessage: String = error.message(
//                    context.getString(R.string.error_cannot_change_protection_code)
//                )
//                val errorToast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
//                errorToast.setGravity(Gravity.CENTER, 0, 0)
//                errorToast.view.setBackgroundColor(
//                    ContextCompat.getColor(
//                        context,
//                        R.color.appColorAccent
//                    )
//                )
//                errorToast.show()
//            }
//        }
    }

    companion object {
        fun newInstance(key: String?): ParentalCodeDialogFragment {
            return ParentalCodeDialogFragment()
        }
    }
}
