package org.alsi.android.tvlaba.tv.vod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.context.model.SessionActivityType.BROWSING_VOD
import org.alsi.android.domain.context.model.SessionActivityType.PLAYBACK_VOD
import org.alsi.android.domain.vod.model.guide.VodStartContext
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationvod.model.VodGuideStartViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.databinding.VodGuideStartFragmentBinding
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject

/**
 * Start destination in VOD Guide's navigation graph.
 *
 * Selects initial destination - VOD Directory, or VOD digest, or
 * VOD playback depending on the last session context, i.e. "guide cursor",
 */
class VodGuideStartFragment : Fragment(R.layout.vod_guide_start_fragment) {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var viewModel: VodGuideStartViewModel

    private var _vb: VodGuideStartFragmentBinding? = null
    private val vb get() = _vb!!

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(VodGuideStartViewModel::class.java)

        viewModel.initWithService(
            arguments?.getLong(getString(R.string.navigation_argument_key_service_id)))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _vb = VodGuideStartFragmentBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.getLiveData().observe(this, { handleGettingStartContext(it) })
        navigateVodDirectory()
    }

    private fun handleGettingStartContext(resource: Resource<VodStartContext>) {
        when (resource.status) {
            ResourceState.LOADING -> {
                showProgress()
            }
            ResourceState.SUCCESS -> {
                dismissProgress()
                handleContextRestored(resource.data)
            }
            ResourceState.ERROR -> {
                dismissProgress()
                errorHandler.run(this, resource.throwable)
            }
            else -> {
            }
        }
    }

    private fun handleContextRestored(startContext: VodStartContext?) {
        startContext?: return
        when (startContext.activity.activityType) {
            BROWSING_VOD -> TODO("Implement initial navigation to browse VOD Directory")
            PLAYBACK_VOD -> TODO("Implement initial navigation to play back VOD item")
            else -> navigateVodDirectory()
        }
    }

    private fun navigateVodDirectory() {
        findNavController(this).navigate(
            VodGuideStartFragmentDirections
                .actionVodGuideStartFragmentToVodDirectoryFragment()
        )
    }

    private fun navigateVodDigest() {
        findNavController(this).navigate(VodGuideStartFragmentDirections
            .actionVodGuideStartFragmentToVodDigestFragment())
    }

    private fun navigateVodPlayback() {
        findNavController(this).navigate(VodGuideStartFragmentDirections
            .actionVodGuideStartFragmentToVodPlaybackFragment())
    }

    private fun showProgress() {
        vb.vodGuideStartProgressView.visibility = View.VISIBLE
    }

    private fun dismissProgress() {
        vb.vodGuideStartProgressView.visibility = View.INVISIBLE
    }
}