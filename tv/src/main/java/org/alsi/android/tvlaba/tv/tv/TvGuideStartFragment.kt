package org.alsi.android.tvlaba.tv.tv

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import org.alsi.android.tvlaba.R
import timber.log.Timber

/**
 * Start destination in TV Guide's navigation graph.
 *
 * Selects initial destination - TV Channels Directory, or TV Program Details, or
 * TV Program & Schedule Fragment depending on the last session context,
 * i.e. "guide cursor",
 */
class TvGuideStartFragment : Fragment(R.layout.tv_guide_start_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO Get the last guide context an decide to which destination navigate

        view.findNavController().addOnDestinationChangedListener { navController, destination, _ ->
            Timber.d("### Destination Changed to %s/%s", navController.previousBackStackEntry?.destination?.id, destination.id)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object:
            OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Timber.d("### OnBackPressedCallback")
                }
            }
        )

        view.findNavController().navigate(
            TvGuideStartFragmentDirections.actionTvGuideStartFragmentToTvChannelDirectoryFragment()
        )

// This method rather works, though could not checked back stack synthesis
//        val deepLinkRequest = NavDeepLinkRequest.Builder
//            .fromUri(
//                "android-app://tvlaba.android.alsi.org/tv/tvPlaybackAndScheduleFragment".toUri()
//            ).build()
//
//        findNavController(this).navigate(deepLinkRequest)
    }
}