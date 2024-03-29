package org.alsi.android.tvlaba.tv.tv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.context.model.SessionActivityType.*
import org.alsi.android.domain.tv.model.guide.TvStartContext
import org.alsi.android.domain.tv.model.session.TvBrowsePage.*
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvGuideStartViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.databinding.TvGuideStartFragmentBinding
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject

/**
 * Start destination in TV Guide's navigation graph.
 *
 * Selects initial destination - TV Channels Directory, or TV Program Details, or
 * TV Program & Schedule Fragment depending on the last session context,
 * i.e. "guide cursor",
 */
class TvGuideStartFragment : Fragment(R.layout.tv_guide_start_fragment) {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var viewModel: TvGuideStartViewModel

    private var _vb: TvGuideStartFragmentBinding? = null
    private val vb get() = _vb!!

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(TvGuideStartViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _vb = TvGuideStartFragmentBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.getLiveData().observe(this, { handleGettingStartContext(it) })
    }

    private fun handleGettingStartContext(resource: Resource<TvStartContext>) {
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

    private fun handleContextRestored(startContext: TvStartContext?) {
        startContext?: return
        when (startContext.activity.activityType) {
            BROWSING_TV -> when (startContext.browse.page) {
                CATEGORIES, CHANNELS -> navigateChannelDirectory()
                PROGRAM -> navigateProgramDetails()
                PLAYBACK -> navigatePlaybackAndSchedule()
                else -> navigateChannelDirectory()
            }
            PLAYBACK_TV -> navigatePlaybackAndSchedule()
            BROWSING_VOD -> TODO("Implement initial navigation to browse VOD Directory")
            PLAYBACK_VOD -> TODO("Implement initial navigation to play back VOD item")
            else -> navigateChannelDirectory()
        }
    }

    private fun navigateChannelDirectory() {
        findNavController(this).navigate(
            TvGuideStartFragmentDirections
                .actionTvGuideStartFragmentToTvChannelDirectoryFragment()
        )
    }

    private fun navigateProgramDetails() {
        findNavController(this).navigate(TvGuideStartFragmentDirections
            .actionTvGuideStartFragmentToTvProgramDetailsFragment())
    }

    private fun navigatePlaybackAndSchedule() {
        findNavController(this).navigate(TvGuideStartFragmentDirections
            .actionTvGuideStartFragmentToTvPlaybackAndScheduleFragment())
    }

    private fun showProgress() {
        vb.tvGuideStartProgressView.visibility = View.VISIBLE
    }

    private fun dismissProgress() {
        vb.tvGuideStartProgressView.visibility = View.INVISIBLE
    }

}