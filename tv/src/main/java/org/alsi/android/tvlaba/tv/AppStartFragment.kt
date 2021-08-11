package org.alsi.android.tvlaba.tv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.context.model.SessionActivityType
import org.alsi.android.presentation.AppStartViewModel
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.databinding.AppStartFragmentBinding
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject

/**
 *  App Start Fragment performs initial navigation depending upon the last session context
 */
class AppStartFragment: Fragment(R.layout.app_start_fragment) {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var appStartViewModel: AppStartViewModel

    private var _vb: AppStartFragmentBinding? = null
    private val vb get() = _vb!!

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        appStartViewModel = ViewModelProvider(this, viewModelFactory)
            .get(AppStartViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _vb = AppStartFragmentBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onStart() {
        super.onStart()

        appStartViewModel.getNavigationTargetLiveData()
            .observe(this, { handleNavigationDataState(it) })
    }

    private fun handleNavigationDataState(resource: Resource<SessionActivityType>) {
        when (resource.status) {
            ResourceState.LOADING -> {
                showProgress()
            }
            ResourceState.SUCCESS -> {
                dismissProgress()
                navigateToInitialTarget(resource.data)
            }
            ResourceState.ERROR -> {
                dismissProgress()
                errorHandler.run(this, resource.throwable)
            }
            else -> {
                dismissProgress()
            }
        }
    }

    private fun navigateToInitialTarget(activityType: SessionActivityType?) {
        val navController = findNavController(this)
        when(activityType) {

            SessionActivityType.NONE, SessionActivityType.LOGIN ->
                navController.navigate(AppStartFragmentDirections.actionAppStartFragmentToLoginFragment())

            SessionActivityType.PLAYBACK_TV,  SessionActivityType.BROWSING_TV ->
                navController.navigate(AppStartFragmentDirections.actionAppStartFragmentToTvGuide())

            else -> navController.navigate(AppStartFragmentDirections.actionAppStartFragmentToTvGuide())
        }
    }

    private fun showProgress() {
        vb.appStartProgressView.visibility = View.VISIBLE
    }

    private fun dismissProgress() {
        vb.appStartProgressView.visibility = View.INVISIBLE
    }
}
