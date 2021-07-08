package org.alsi.android.tvlaba.tv

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import org.alsi.android.tvlaba.R

/**
 *  App Start Fragment performs initial navigation depending upon the last session context
 */
class AppStartFragment: Fragment(R.layout.app_start_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findNavController().navigate(
            AppStartFragmentDirections.actionAppStartFragmentToTvGuide()
        )
    }
}