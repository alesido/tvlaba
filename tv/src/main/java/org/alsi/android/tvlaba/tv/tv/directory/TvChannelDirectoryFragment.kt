package org.alsi.android.tvlaba.tv.tv.directory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.ListRowPresenter.SelectItemViewHolderTask
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelDirectoryPosition
import org.alsi.android.domain.tv.model.guide.TvChannelListWindow
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvChannelDirectoryBrowseLiveData
import org.alsi.android.presentationtv.model.TvChannelDirectoryBrowseViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.model.CardMenuItem
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * @see "https://medium.com/@iammert/new-android-injector-with-dagger-2-part-1-8baa60152abe"
 */
class TvChannelDirectoryFragment : BrowseSupportFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var browseViewModel : TvChannelDirectoryBrowseViewModel

    private var liveTimeIndicatorTaskSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        browseViewModel = ViewModelProvider(this, viewModelFactory)
                .get(TvChannelDirectoryBrowseViewModel::class.java)

        adapter = ArrayObjectAdapter( ListRowPresenter())

        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is TvChannel) {
                // navigate to program details fragment
                browseViewModel.onChannelAction(item) {
                    Navigation.findNavController(requireActivity(), R.id.tvGuideNavigationHost)
                            .navigate(TvChannelDirectoryFragmentDirections
                                    .actionTvChannelDirectoryFragmentToTvProgramDetailsFragment())
                }
            }
            else if (item is CardMenuItem) {
                when(item.title) {
                    "VOD" -> NavHostFragment.findNavController(this)
                        .navigate(R.id.actionGlobalNavigateVodSection)
                    "Settings" -> println("Menu action \"${item.title}\"")
                }
            }
        }

        setOnItemViewSelectedListener { _, item, rowViewHolder, _ ->
            if (item is TvChannel) {
                // record current browsing position to restore on the next start
                val rowPosition = this@TvChannelDirectoryFragment.selectedPosition
                val itemPosition = (rowViewHolder as ListRowPresenter.ViewHolder)
                        .gridView.selectedPosition
                browseViewModel.onChannelSelected(
                    if (rowPosition > 0) rowPosition - 1 else 0, itemPosition, item)
                // schedule next channel lives update
                browseViewModel.onItemsVisibilityChange(
                        visibleChannelDirectoryItemIds()
                )
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup

        val progressView = inflater.inflate(R.layout.progress_view_common, view, false)
        view.addView(progressView)

        progressBarManager.enableProgressBar()
        progressBarManager.setProgressBarView(progressView)

        return view
    }

    override fun onStart() {
        super.onStart()
        browseViewModel.getLiveDirectory().observe(this, {
            if (it != null) handleCategoriesListDataState(it)
        })
    }

    override fun onResume() {
        super.onResume()
        browseViewModel.onResume()
        startLiveTimeIndicatorTask()
    }

    override fun onPause() {
        super.onPause()
        browseViewModel.onPause()
        stopLiveTimeIndicatorTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        browseViewModel.dispose()
    }

    /**
     * @see "https://stackoverflow.com/questions/54954232/find-first-visible-position-in-leanback-gridlayoutmanager"
     */
    private fun visibleChannelDirectoryItemIds(): TvChannelListWindow {
        rowsSupportFragment?: return TvChannelListWindow(listOf(), System.currentTimeMillis())
        val visibleItemsIds: MutableList<Long> = mutableListOf()
        for (i in 0 until adapter.size()) {
            val rowViewHolder = rowsSupportFragment.findRowViewHolderByPosition(i)?: continue
            val rowView: HorizontalGridView = (rowViewHolder as ListRowPresenter.ViewHolder).gridView?: continue
            findVisibleItemsOfHorizontalRow(rowView.layoutManager).map { node ->
                rowView.adapter?.getItemId(node.first)?.let{
                    visibleItemsIds.add(it)
                }
            }
        }
        return TvChannelListWindow(visibleItemsIds, System.currentTimeMillis())
    }

    private fun updateVisibleChannelDirectoryItems() {
        rowsSupportFragment?: return
        for (i in 0 until adapter.size()) {
            val rowViewHolder = rowsSupportFragment.findRowViewHolderByPosition(i)?: continue
            val rowView: HorizontalGridView = (rowViewHolder as ListRowPresenter.ViewHolder).gridView?: continue
            findVisibleItemsOfHorizontalRow(rowView.layoutManager).map { node ->
                rowView.adapter?.notifyItemChanged(node.first)
            }
        }
    }

    private fun findVisibleItemsOfHorizontalRow(
            layoutManager: RecyclerView.LayoutManager?
    ) : List<Pair<Int, View>> {
        val result: MutableList<Pair<Int, View>> = mutableListOf()
        layoutManager?: return result
        val orientationHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        val viewPortStart = orientationHelper.startAfterPadding
        val viewPortEnd = orientationHelper.endAfterPadding
        for (i in 0 until layoutManager.childCount) {
            val child = layoutManager.getChildAt(i)
            child?:continue
            val childStart = orientationHelper.getDecoratedStart(child)
            val childEnd = orientationHelper.getDecoratedEnd(child)
            if (childEnd < viewPortStart) continue
            if (childStart > viewPortEnd) break
            result.add(Pair(i, child))
        }
        return result
    }

    private fun handleCategoriesListDataState(resource: Resource<TvChannelDirectoryBrowseLiveData>) {
        when (resource.status) {
            ResourceState.LOADING -> {
                progressBarManager.show()
            }
            ResourceState.SUCCESS -> {
                progressBarManager.hide()
                if (adapter.size() == 0)
                    loadDirectoryView(resource.data)
                else
                    updateDirectoryView(resource.data)
            }
            ResourceState.ERROR -> {
                progressBarManager.hide()
                errorHandler.run(this, resource.throwable)
            }
            else -> {
            }
        }
    }

    private fun loadDirectoryView(data: TvChannelDirectoryBrowseLiveData?) {
        data?.directory?.let { directory ->
            // recreate category channels rows
            val categoryRows = directory.categories.mapIndexed { idx, category ->
                val header = HeaderItem((idx + 1).toLong(), category.title)
                val listRowAdapter = TvCategoryChannelsListRowAdapter(
                        TvDirectoryChannelCardPresenter()).apply {
                    setItems(directory.index[category.id], null)
                }
                ListRow(header, listRowAdapter)
            }

            // add top and bottom menu
            val topMenuHeader = HeaderItem(0L, getString(R.string.label_menu))
            val bottomMenuHeader = HeaderItem((categoryRows.size + 1).toLong(), getString(R.string.label_menu))
            val mixedRows: MutableList<ListRow> = mutableListOf()
            val menuRowAdapter = TvMenuRowAdapter(TvMenuCardPresenter()).apply {
                setItems(listOf(
                    CardMenuItem(1,"VOD", 0),
                    CardMenuItem(2,"Settings", 0),
                ), null)
            }
            mixedRows.add(ListRow(topMenuHeader, menuRowAdapter))
            mixedRows.addAll(categoryRows)
            mixedRows.add(ListRow(bottomMenuHeader, menuRowAdapter))

            // set fresh new rows to the adapter
            (adapter as ArrayObjectAdapter).setItems(mixedRows, null)
            // ensure correct initial position & schedule update
            onRowsLayoutReady(data.position)
        }
    }

    private fun updateDirectoryView(data: TvChannelDirectoryBrowseLiveData?) {
        data?.directory?: return
        for (i in 1 until adapter.size() - 2) {
            data.directory.index[data.directory.categories[i].id]?.let {
                ((adapter[i] as ListRow).adapter as ArrayObjectAdapter)
                    .setItems(it, tvCategoryChannelsDiff)
            }
        }
    }

    private val tvCategoryChannelsDiff = TvCategoryChannelsDiff()
    class TvCategoryChannelsDiff : DiffCallback<TvChannel>() {
        override fun areItemsTheSame(oldItem: TvChannel, newItem: TvChannel) = oldItem.id == newItem.id
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: TvChannel, newItem: TvChannel) = oldItem == newItem
    }

    private fun selectDirectoryPosition(position: TvChannelDirectoryPosition?) {
        position?.let {
            Timber.d("@selectDirectoryPosition %s", position)
            val setItemPositionTask = if (it.channelIndex > 0) SelectItemViewHolderTask(it.channelIndex) else null
            // ^^^ convention: show categories when a 1st channel in the list to be selected
            setItemPositionTask?.isSmoothScroll = false
            setSelectedPosition(it.categoryIndex, false, setItemPositionTask)
        }
    }

    /**
     * @see "https://antonioleiva.com/kotlin-ongloballayoutlistener/"
     */
    private fun onRowsLayoutReady(initialPosition: TvChannelDirectoryPosition?) {
        view?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                rowsSupportFragment?.let {
                    val visibleItems = visibleChannelDirectoryItemIds()
                    if (visibleItems.ids.isNotEmpty()) {
                        // stop observation
                        view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                        // select initial position
                        selectDirectoryPosition(initialPosition)
                        // schedule channel items update
                        browseViewModel.onItemsVisibilityChange(visibleItems)
                        startLiveTimeIndicatorTask()
                    }
                }
            }
        })
    }

    private fun startLiveTimeIndicatorTask() {
        stopLiveTimeIndicatorTask()
        liveTimeIndicatorTaskSubscription = Flowable.interval(
                TIMER_INTERVAL_MINUTES_LIVE_TIME_TASK, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { updateVisibleChannelDirectoryItems() },
                        { error -> Timber.w(error,"@startListUpdateTimerTask") },
                        { Timber.d("@startListUpdateTimerTask, completed") }
                )
    }

    private fun stopLiveTimeIndicatorTask() {
        liveTimeIndicatorTaskSubscription?.let {
            if (! it.isDisposed) it.dispose()
        }
    }

    companion object {
        const val TIMER_INTERVAL_MINUTES_LIVE_TIME_TASK = 1L
    }
}

class TvCategoryChannelsListRowAdapter(presenter: Presenter): ArrayObjectAdapter(presenter) {
    override fun getId(position: Int): Long {
        return (get(position) as TvChannel).id
    }
}

class TvMenuRowAdapter(presenter: Presenter): ArrayObjectAdapter(presenter) {
    override fun getId(position: Int): Long {
        return (get(position) as CardMenuItem).id
    }
}

