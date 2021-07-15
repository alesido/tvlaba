package org.alsi.android.tvlaba.tv

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.context.model.SessionActivityType
import org.alsi.android.presentation.AppStartViewModel
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject

/**
 *  App Start Fragment performs initial navigation depending upon the last session context
 */
class AppStartFragment: Fragment(R.layout.app_start_fragment) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var appStartViewModel: AppStartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        appStartViewModel = ViewModelProvider(this, viewModelFactory)
            .get(AppStartViewModel::class.java)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        view.findNavController().navigate(
//            AppStartFragmentDirections.actionAppStartFragmentToTvGuide()
//        )
//    }

    override fun onStart() {
        super.onStart()

        appStartViewModel.getNavigationTargetLiveData()
            .observe(this, { handleNavigationDataState(it) })
    }

    private fun handleNavigationDataState(resource: Resource<SessionActivityType>) {
        when (resource.status) {
            ResourceState.LOADING -> {
                //progressBarManager.show()
            }
            ResourceState.SUCCESS -> {
                //progressBarManager.hide()
                navigateToInitialTarget(resource.data)
            }
            ResourceState.ERROR -> {
                //progressBarManager.hide()
            }
            else -> {
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
        }
    }
}