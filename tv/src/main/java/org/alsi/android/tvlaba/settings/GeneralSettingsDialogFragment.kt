package org.alsi.android.tvlaba.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import org.alsi.android.tvlaba.R

class GeneralSettingsDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.general_settings_dialog_fragment, container)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.setLayout(width, height)
        }
    }

    companion object {
        fun newInstance(): GeneralSettingsDialogFragment {
            val dialog = GeneralSettingsDialogFragment()
            dialog.setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
            return dialog
        }
    }
}