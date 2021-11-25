package org.alsi.android.tvlaba.tv.tv.directory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.ListRowPresenter.SelectItemViewHolderTask
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.presentation.settings.ParentalControlViewModel
import org.alsi.android.presentation.settings.ParentalControlViewModel.ParentalEventKind
import org.alsi.android.presentation.state.Event
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvChannelDirectoryBrowseLiveData
import org.alsi.android.presentationtv.model.TvChannelDirectoryBrowseViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.settings.GeneralSettingsDialogFragment
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.model.CardMenuItem
import org.alsi.android.tvlaba.tv.tv.parental.ParentalControlCheckInFragment
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
    private lateinit var parentalViewModel: ParentalControlViewModel
    private val parentalEventObserver = ParentalEventObserver()

    private var liveTimeIndicatorTaskSubscription: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        browseViewModel = ViewModelProvider(this, viewModelFactory)
                .get(TvChannelDirectoryBrowseViewModel::class.java)
        parentalViewModel = ViewModelProvider(
            requireActivity(), // shared view model
            viewModelFactory
        ).get(ParentalControlViewModel::class.java)

        adapter = ArrayObjectAdapter( ListRowPresenter())

        setupClickListener()
        setupSelectListener()
    }

    private fun setupClickListener() {
        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is TvChannel) {
                if (parentalViewModel.isAccessAllowed(item)) {
                    // navigate to program details or playback fragment
                    browseViewModel.onChannelAction(item) { navigateOnChannelAction(item) }
                }
                else {
                    openParentalAuthorization()
                }
            }
            else if (item is CardMenuItem) {
                if (item.id == MENU_ITEM_SETTINGS_ID) {
                    GeneralSettingsDialogFragment.newInstance()
                        .show(childFragmentManager,
                            GeneralSettingsDialogFragment::class.java.simpleName)
                }
                else if (item.payload != null && item.payload is StreamingServicePresentation) {
                    with(item.payload) {
                        when(kind) {
                            StreamingServiceKind.TV -> navigateToTv(serviceId)
                            StreamingServiceKind.VOD -> navigateToVod(serviceId)
                            else -> navigateToTv(serviceId)
                        }
                    }
                }
            }
        }
    }

    private fun setupSelectListener() {
        setOnItemViewSelectedListener { _, item, rowViewHolder, _ ->
            if (item != null) {
                // record current browsing position to restore on the next start
                val rowPosition = this@TvChannelDirectoryFragment.selectedPosition
                val itemPosition = (rowViewHolder as ListRowPresenter.ViewHolder)
                    .gridView.selectedPosition
                browseViewModel.onListingItemSelected(rowPosition, itemPosition, item)
                // schedule next channel lives update
                browseViewModel.onItemsVisibilityChange(visibleChannelDirectoryItemIds())
            }
        }
    }

    private fun openParentalAuthorization() {
        parentalViewModel.getEventChannel().observe(this, parentalEventObserver)
        val fragment = ParentalControlCheckInFragment()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        fragment.uiStyle = GuidedStepSupportFragment.UI_STYLE_ENTRANCE
        transaction
            .replace(android.R.id.content, fragment, fragment.javaClass.toString())
            .commit()
    }

    private inner class ParentalEventObserver: Observer<Event<ParentalEventKind>> {
        override fun onChanged(t: Event<ParentalEventKind>?) {
            t?.contentIfNotHandled?.let { kind -> handleParentalControlEvent(kind, t.payload) }
        }
    }

    private fun navigateOnChannelAction(channel: TvChannel) {
        NavHostFragment.findNavController(this).navigate(
            if (channel.features.hasSchedule) TvChannelDirectoryFragmentDirections
                .actionTvChannelDirectoryFragmentToTvProgramDetailsFragment()
            else TvChannelDirectoryFragmentDirections
                .actionTvChannelDirectoryFragmentToTvPlaybackAndScheduleFragment()
        )
    }

    private fun navigateToTv(serviceId: Long) {
        val nc = NavHostFragment.findNavController(this)
        nc.popBackStack()
        nc.navigate(R.id.actionGlobalNavigateTvSection, bundleOf(
            getString(R.string.navigation_argument_key_service_id) to serviceId))
    }

    private fun navigateToVod(serviceId: Long) {
        val nc = NavHostFragment.findNavController(this)
        nc.popBackStack()
        nc.navigate(R.id.actionGlobalNavigateVodSection, bundleOf(
            getString(R.string.navigation_argument_key_service_id) to serviceId))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup

        addBackPressedCallback()

        val progressView = inflater.inflate(R.layout.progress_view_common, view, false)
        view.addView(progressView)

        progressBarManager.enableProgressBar()
        progressBarManager.setProgressBarView(progressView)

        return view
    }

    private fun addBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object:
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    remove() // remove this listener
                    requireActivity().finish()
                }
            }
        )
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
                    updateDirectoryView2(resource.data)
            }
            ResourceState.ERROR -> {
                progressBarManager.hide()
                errorHandler.run(this, resource.throwable)
            }
            else -> {
            }
        }
    }

    private fun handleParentalControlEvent(kind: ParentalEventKind, payload: Any?) {
        if (kind == ParentalEventKind.ACCESS_GRANTED && payload is TvChannel) {
            browseViewModel.onChannelAction(payload) { navigateOnChannelAction(payload) }
            parentalViewModel.getEventChannel().removeObserver(parentalEventObserver)
        }
    }

    private fun loadDirectoryView(data: TvChannelDirectoryBrowseLiveData?) {
        data?.directory?.let { directory ->
            browseViewModel.currentPresentation?.title?.let { title = it }

            // category channel rows
            val categoryChannelsRows = categoryChannelRows(directory)

            // top and bottom menu
            val topMenuHeader = HeaderItem(-1L, getString(R.string.label_menu))
            val bottomMenuHeader = HeaderItem(-2L, getString(R.string.label_menu))
            val menuRowAdapter = menuRowAdapter()

            // combine menu and category channel rows
            val mixedRows = mutableListOf<ListRow>().apply {
                add(ListRow(topMenuHeader, menuRowAdapter))
                addAll(categoryChannelsRows)
                add(ListRow(bottomMenuHeader, menuRowAdapter))
            }

            // set rows to the adapter
            (adapter as ArrayObjectAdapter).setItems(mixedRows, null)

            // ensure correct initial position & schedule update
            onRowsLayoutReady(data.position)
        }
    }

    private fun categoryChannelRows(directory: TvChannelDirectory) = directory.categories.filter {
        directory.index[it.id]?.isNotEmpty() == true // skip category with empty list of channels
    }.map { category -> categoryChannelRow(directory, category) }

    private fun categoryChannelRow(directory: TvChannelDirectory, category: TvChannelCategory)
    = ListRow(
        HeaderItem(category.id, category.title),
        TvCategoryChannelsListRowAdapter(TvDirectoryChannelCardPresenter()).apply {
            setItems(directory.index[category.id], null)
        })


    private fun menuRowAdapter() = TvMenuRowAdapter(TvMenuCardPresenter()).apply {
        val items: MutableList<CardMenuItem> = mutableListOf()

        items.addAll(browseViewModel.vodPresentations.mapIndexed { idx, item ->
            CardMenuItem(MENU_ITEM_VOD_BASE_ID + idx.toLong(),
                item.title, payload = item) })

        items.addAll(browseViewModel.tvPresentations.mapIndexed { idx, item ->
            CardMenuItem(MENU_ITEM_TV_BASE_ID + idx.toLong(),
                item.title, payload = item) })

        items.add(CardMenuItem(MENU_ITEM_SETTINGS_ID,
            getString(R.string.label_menu_settings), R.drawable.settings_icon_metal))

        setItems(items, null)
    }

    /**
     * This method just updates already listed categories. Cannot add or remove category items.
     * The method is saved in debugging diagnostics purposes.
     */
    private fun updateDirectoryView(data: TvChannelDirectoryBrowseLiveData?) {
        data?.directory?:
            return
        if (data.directory.categories.isEmpty())
            return
        val totalCategories = data.directory.categories.size
        for (i in 1 until adapter.size() - 1) {
            if (i >= totalCategories)
                continue
            val channels = data.directory.index[data.directory.categories[i - 1].id]
            if (channels?.isNotEmpty() != true)
                continue
            with (adapter[i] as ListRow) {
                val category = data.directory.categories[i - 1]
                headerItem = HeaderItem(category.id, category.title)
                (adapter as ArrayObjectAdapter).setItems(channels, tvCategoryChannelsDiff)
            }
        }
    }

    /**
     *  Adding or removing categories have to be evident. This way, in case some categories
     *  added or removed it will be appropriate to show user a message.
     *
     *  Only language of categories and live programs data may be changed silently.
     */
    private fun updateDirectoryView2(data: TvChannelDirectoryBrowseLiveData?) {
        if (null == data?.directory || data.directory.categories.isEmpty())
            return
        // prepare data to check if there are new and/or removed category items
        val indexByCategoryId: LinkedHashMap<Long, ListRow> = LinkedHashMap()
        for (i in 1 until adapter.size() - 1) {
            val row = adapter[i] as ListRow
            indexByCategoryId[row.headerItem.id] = row
        }
        // collect category items to remove and to add
        val toRemove = indexByCategoryId.filterKeys { null == data.directory.categoryById[it] }
        val toAdd = data.directory.categoryById.filterKeys { null == indexByCategoryId[it] }
        // add/remove categories
        val isDirectoryAlreadyLoaded = adapter.size() > 2
        if (isDirectoryAlreadyLoaded && (toRemove.isNotEmpty() || toAdd.isNotEmpty())) {
            // notify user unobtrusively on categories set change
            showMessage(R.string.message_tv_categories_set_changed)
            // remove collected to remove
            toRemove.forEach { (adapter as ArrayObjectAdapter).remove(it.value) }
            // add collected to add
            data.directory.categories.forEachIndexed { position, category ->
                if (toAdd.containsKey(category.id))
                    (adapter as ArrayObjectAdapter).add(position + 1,
                        categoryChannelRow(data.directory, category))
            }
        }
        // update categories
        for (i in 1 until adapter.size() - 1) {
            val category = data.directory.categories[i - 1]
            if (toAdd.isNotEmpty() && toAdd.containsKey(category.id))
                continue // no sense to update fresh new category content
            with (adapter[i] as ListRow) {
                headerItem = HeaderItem(category.id, category.title)
                (adapter as ArrayObjectAdapter).setItems(
                    data.directory.index[category.id],
                    tvCategoryChannelsDiff)
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

    private fun showMessage(messageRes: Int) {
        val toast = Toast.makeText(requireContext(), getString(messageRes), Toast.LENGTH_SHORT)
        with(toast) {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
            view.setPadding(24)
        }
        toast.show()
    }

    companion object {
        const val TIMER_INTERVAL_MINUTES_LIVE_TIME_TASK = 1L

        const val MENU_ITEM_VOD_BASE_ID = 1001L
        const val MENU_ITEM_TV_BASE_ID = 1200L
        const val MENU_ITEM_SETTINGS_ID = 1300L
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

