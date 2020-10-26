package org.alsi.android.tvlaba.tv.tv.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import org.alsi.android.tvlaba.R

class TvPlaybackPreferencesDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tv_playback_preferences_dialog_fragment, container)
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
        fun newInstance(): TvPlaybackPreferencesDialogFragment {
            val dialog = TvPlaybackPreferencesDialogFragment()
            dialog.setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
            return dialog
        }
    }
}