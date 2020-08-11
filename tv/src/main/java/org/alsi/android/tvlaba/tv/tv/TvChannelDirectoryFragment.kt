package org.alsi.android.tvlaba.tv.tv

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvChannelDirectoryBrowseViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.tv.directory.TvDirectoryChannelCardPresenter
import javax.inject.Inject

/**
 * @see "https://medium.com/@iammert/new-android-injector-with-dagger-2-part-1-8baa60152abe"
 */
class TvChannelDirectoryFragment : BrowseSupportFragment() {

    private lateinit var browseViewModel : TvChannelDirectoryBrowseViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        browseViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvChannelDirectoryBrowseViewModel::class.java)

        adapter = ArrayObjectAdapter(ListRowPresenter())

        setOnItemViewClickedListener {_, item, _, _ ->
            if (item is TvChannel) {
                browseViewModel.onChannelAction(item) {
                    Navigation.findNavController(requireActivity(), R.id.tvGuideNavigationHost)
                            .navigate(TvChannelDirectoryFragmentDirections
                                    .actionTvChannelDirectoryFragmentToTvPlaybackAndScheduleFragment())
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        browseViewModel.getLiveData().observe(this,
                Observer<Resource<TvChannelDirectory>> {
                    if (it != null) handleCategoriesListDataState(it)
                })
    }

    private fun handleCategoriesListDataState(resource: Resource<TvChannelDirectory>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                setupScreenForSuccess(resource.data)
            }
            ResourceState.LOADING -> {
            }
            ResourceState.ERROR -> {
            }
            else -> {
            }
        }
    }

    private fun setupScreenForSuccess(directory: TvChannelDirectory?) {
        directory?.let {
            val categoryRows = directory.categories.mapIndexed { idx, category ->
                val header = HeaderItem(idx.toLong(), category.title)
                val listRowAdapter = ArrayObjectAdapter(TvDirectoryChannelCardPresenter()).apply {
                    setItems(directory.index[category.id], null)
                }
                ListRow(header, listRowAdapter)
            }
            (adapter as ArrayObjectAdapter).setItems(categoryRows, null)
        }
    }
}