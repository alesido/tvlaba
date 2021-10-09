package org.alsi.android.tvlaba.tv

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.AppRoute.*

/**
 *  App Router resolves conditional navigation actions on the top level of app navigation graph.
 */
class AppRouterFragment : Fragment(R.layout.app_start_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            when (AppRouterFragmentArgs.fromBundle(it).route) {
                OnLogIn -> {
                    // TODO navigate to last visited section TV or VOD
                    findNavController(this).navigate(
                        AppRouterFragmentDirections.actionAppRouterFragmentToTvGuideNavigation()
                    )
                }

                TvSection -> {
                    val argKey = getString(R.string.navigation_argument_key_service_id)
                    findNavController(this).navigate(
                        R.id.action_appRouterFragment_to_tvGuideNavigation,
                        bundleOf(argKey to arguments?.getInt(argKey)?.toLong())
                    )
                }

                VodSection -> {
                    val argKey = getString(R.string.navigation_argument_key_service_id)
                    findNavController(this).navigate(
                        R.id.action_appRouterFragment_to_vodGuideNavigation,
                        bundleOf(argKey to arguments?.getInt(argKey)?.toLong())
                    )
                }

                OnSessionInvalid, OnContractInvalid -> {
                    findNavController(this).navigate(
                        AppRouterFragmentDirections.actionAppRouterFragmentToLoginFragment()
                    )
                }
                LogOut -> {
                    // fulfil log out and navigate to login screen
                    findNavController(this).navigate(
                        AppRouterFragmentDirections.actionAppRouterFragmentToLoginFragment()
                    )
                }
            }
        }
    }
}