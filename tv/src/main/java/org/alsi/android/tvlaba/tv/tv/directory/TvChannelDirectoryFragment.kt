package org.alsi.android.tvlaba.tv.tv.directory

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.ListRowPresenter.SelectItemViewHolderTask
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectoryPosition
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvChannelDirectoryBrowseViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import javax.inject.Inject

/**
 * @see "https://medium.com/@iammert/new-android-injector-with-dagger-2-part-1-8baa60152abe"
 */
class TvChannelDirectoryFragment : BrowseSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var browseViewModel : TvChannelDirectoryBrowseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        browseViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvChannelDirectoryBrowseViewModel::class.java)

        adapter = ArrayObjectAdapter(ListRowPresenter())

        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is TvChannel) {
                browseViewModel.onChannelAction(item) {
                    Navigation.findNavController(requireActivity(), R.id.tvGuideNavigationHost)
                            .navigate(TvChannelDirectoryFragmentDirections
//                                            .actionTvChannelDirectoryFragmentToTvPlaybackAndScheduleFragment())
                                            .actionTvChannelDirectoryFragmentToTvProgramDetailsFragment())
                }
            }
        }

        setOnItemViewSelectedListener { _, item, rowViewHolder, _ ->
            if (item is TvChannel) {
                val rowPosition = this@TvChannelDirectoryFragment.selectedPosition
                val itemPosition = (rowViewHolder as ListRowPresenter.ViewHolder)
                        .gridView.selectedPosition - 1 // extra row for search?
                browseViewModel.onChannelSelected(rowPosition, itemPosition, item)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        browseViewModel.getLiveDirectory().observe(this,
                Observer<Resource<TvChannelDirectory>> {
                    if (it != null) handleCategoriesListDataState(it)
                })
        browseViewModel.getLiveDirectoryPosition().observe(this,
                Observer<Resource<TvChannelDirectoryPosition>> {
                    if (it != null) handleDirectoryPositionChange(it)
                })
    }

    override fun onResume() {
        super.onResume()
        browseViewModel.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        browseViewModel.dispose()
    }

    private fun handleCategoriesListDataState(resource: Resource<TvChannelDirectory>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                updateDirectoryView(resource.data)
            }
            ResourceState.LOADING -> {
            }
            ResourceState.ERROR -> {
            }
            else -> {
            }
        }
    }

    private fun handleDirectoryPositionChange(resource: Resource<TvChannelDirectoryPosition>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                selectDirectoryPosition(resource.data)
            }
            ResourceState.LOADING -> {
            }
            ResourceState.ERROR -> {
            }
            else -> {
            }
        }
    }

    private fun updateDirectoryView(directory: TvChannelDirectory?) {
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

    private fun selectDirectoryPosition(position: TvChannelDirectoryPosition?) {
        position?.let {
            setSelectedPosition(it.categoryIndex, true,
                    SelectItemViewHolderTask(it.channelIndex)
            )
        }
    }
}