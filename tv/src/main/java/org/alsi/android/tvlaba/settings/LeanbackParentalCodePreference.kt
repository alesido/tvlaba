package org.alsi.android.tvlaba.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.preference.DialogPreference

class LeanbackParentalCodePreference(context: Context?, attrs: AttributeSet?) :
    DialogPreference(context, attrs) {
    override fun getDialogLayoutResource(): Int {
        return 0//R.layout.parental_code_dialog_leanback
    }

    class ParentCodeDialogFragment : DialogFragment() {
//        @BindView(R.id.oldCodeEdit)
//        var oldCodeEdit: EditText? = null
//
//        @BindView(R.id.newCodeEdit)
//        var newCodeEdit: EditText? = null
//
//        @BindView(R.id.newCodeConfirmEdit)
//        var newCodeConfirmEdit: EditText? = null
//        private var softKeyboard: SoftKeyboard? = null
//        @OnClick(R.id.parental_code_cancel)
//        fun onCancel() {
//            dismiss()
//        }
//
//        @OnClick(R.id.parental_code_submit)
//        fun onSubmit() {
//            submit()
//        }

        override fun onAttach(context: Context) {
            super.onAttach(context)
//            (context as Activity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
//            softKeyboard = SoftKeyboard(context)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
//            @SuppressLint("InflateParams") val view: View =
//                inflater.inflate(R.layout.parental_code_dialog_leanback, null)
//            ButterKnife.bind(this, view)
//            return view
            return null
        }

        override fun onResume() {
            super.onResume()
//            newCodeEdit!!.post {
//                newCodeEdit!!.requestFocus()
//                softKeyboard.hide(oldCodeEdit)
//            }
        }

        private fun submit() {
//            val context = newCodeEdit!!.context ?: return
//            val newCodeText = newCodeEdit!!.text.toString()
//            val newCodeConfirmText = newCodeConfirmEdit!!.text.toString()
//            if (newCodeText.isEmpty() && newCodeConfirmText.isEmpty()) {
//                Toast.makeText(
//                    context,
//                    R.string.preferences_user_category_parents_code_dialog_message_codes_empty,
//                    Toast.LENGTH_SHORT
//                ).show()
//                return
//            }
//            if (newCodeText != newCodeConfirmText) {
//                Toast.makeText(
//                    context,
//                    R.string.preferences_user_category_parents_code_dialog_message_codes_not_equals,
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                App.getComponent().getTvDataSource().changeParentCode(
//                    oldCodeEdit!!.text.toString(), newCodeEdit!!.text.toString(), { dismiss() }
//                ) { error ->
//                    val errorMessage: String = error.message(
//                        context.getString(R.string.error_cannot_change_protection_code)
//                    )
//                    val errorToast = Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
//                    errorToast.setGravity(Gravity.CENTER, 0, 0)
//                    errorToast.view.setBackgroundColor(
//                        ContextCompat.getColor(
//                            context,
//                            R.color.red_400
//                        )
//                    )
//                    errorToast.show()
//                }
//            }
        }

        companion object {
            fun newInstance(key: String?): ParentCodeDialogFragment {
                return ParentCodeDialogFragment()
            }
        }
    }

    init {
//        dialogLayoutResource = R.layout.change_parent_code_dialog
//        setPositiveButtonText(R.string.dialog_button_ok)
//        setNegativeButtonText(R.string.dialog_button_cancel)
    }
}

