package org.alsi.android.tvlaba.tv.vod.directory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.streaming.model.service.StreamingServicePresentation
import org.alsi.android.domain.vod.model.guide.directory.VodDirectoryPosition
import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationvod.model.VodDirectoryBrowseLiveData
import org.alsi.android.presentationvod.model.VodDirectoryBrowseViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.settings.GeneralSettingsDialogFragment
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.model.CardMenuItem
import javax.inject.Inject

class VodDirectoryFragment : BrowseSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var browseViewModel : VodDirectoryBrowseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        // UI
        title = getString(R.string.app_section_vod)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // data
        browseViewModel = ViewModelProvider(this, viewModelFactory)
            .get(VodDirectoryBrowseViewModel::class.java)

        adapter = ArrayObjectAdapter( ListRowPresenter())

        setSelectedListener()
        setClickedListener()
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
            viewLifecycleOwner, backPressedCallback
        )
    }

    private val backPressedCallback: OnBackPressedCallback = object:
        OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            remove() // remove this listener
            requireActivity().finish()
        }
    }

    private fun setSelectedListener() {

        setOnItemViewSelectedListener { _, item, rowViewHolder, row ->
            if (null == item && row != null) {
                // an empty row selected or too early selection (?)
                if (row.id > 0) {
                    // ID of a row by convention is an index of a VOD Unit if it > 0
                    browseViewModel.onUnitSelected((row.id - 1).toInt())
                }
                return@setOnItemViewSelectedListener
            }
            when (item) {
                is CardMenuItem -> {
                    if (item.payload is VodSection) {
                        title = item.payload.title
                        browseViewModel.onSectionSelected(item.payload)
                    }
                }
                is VodUnit -> browseViewModel.onUnitSelected(item)
                is VodListingItem -> browseViewModel.onListingItemSelected(item,
                    (rowViewHolder as ListRowPresenter.ViewHolder).gridView.selectedPosition)
            }
        }
    }

    private fun setClickedListener() {
        setOnItemViewClickedListener { _, item, rowViewHolder, _ ->
            when (item) {

                is CardMenuItem -> {
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

                is VodListingItem -> {
                    browseViewModel.onListingItemAction(item,
                        (rowViewHolder as ListRowPresenter.ViewHolder).gridView.selectedPosition) {
                        NavHostFragment.findNavController(this)
                            .navigate(VodDirectoryFragmentDirections
                                .actionVodDirectoryFragmentToVodDigestFragment())
                    }
                }
            }
        }
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

    override fun onStart() {
        super.onStart()
        browseViewModel.getLiveDirectory().observe(this, {
            handleDirectoryDataState(it)
        })
    }

    override fun onResume() {
        super.onResume()
        browseViewModel.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        browseViewModel.dispose()
        //backPressedCallback.remove()
    }

    private fun handleDirectoryDataState(resource: Resource<VodDirectoryBrowseLiveData>) {
        when (resource.status) {
            ResourceState.LOADING -> {
                progressBarManager.show()
            }
            ResourceState.SUCCESS -> {
                progressBarManager.hide()
                resource.data?.let {
                    if (adapter.size() == 0)
                        loadDirectoryView(resource.data)
                    else
                        updateDirectoryView(resource.data)
                }
            }
            ResourceState.ERROR -> {
                progressBarManager.hide()
                errorHandler.run(this, resource.throwable)
            }
        }
    }

    private fun loadDirectoryView(browseData: VodDirectoryBrowseLiveData?) {
        browseData?.directory?.let { directory ->
            browseViewModel.currentPresentation?.title?.let { title = it }

            // row: sections + menu
            val menuHeader = HeaderItem(0L, getString(R.string.label_vod_sections_and_navigation))
            val menuItems: MutableList<CardMenuItem> = mutableListOf()
            // .. sections
            if (directory.sections.size > 1 || directory.sections[0].isSectionSubstitute != true) {
                menuItems.addAll(directory.sections.mapIndexed { index, section ->
                    CardMenuItem(index.toLong(), section.title, payload = section)
                })
            }
            // .. navigation

            menuItems.addAll(browseViewModel.tvPresentations.mapIndexed { idx, item ->
                CardMenuItem(MENU_ITEM_TV_BASE_ID + idx.toLong(), item.title, payload = item) })
            menuItems.addAll(browseViewModel.vodPresentations.mapIndexed { idx, item ->
                CardMenuItem(MENU_ITEM_VOD_BASE_ID + idx.toLong(), item.title, payload = item) })
            menuItems.add(CardMenuItem(MENU_ITEM_SETTINGS_ID,
                getString(R.string.label_menu_settings), R.drawable.settings_icon_metal))

            // row adapter
            val menuRowAdapter = VodMenuRowAdapter(VodMenuCardPresenter()).apply {
                setItems(menuItems, null)
            }

            // units
            val unitRows = directory.sections[browseData.position.sectionIndex].units.mapIndexed { index, unit ->
                val header = HeaderItem((index + 1).toLong(), unit.title)
                val listRowAdapter = VodUnitListRowAdapter(VodItemCardPresenter()).apply {
                    setItems(unit.window?.items?: listOf(VodListingItem.empty()), null)
                }
                ListRow(header, listRowAdapter)
            }

            // all rows
            val mixedRows: MutableList<ListRow> = mutableListOf()
            mixedRows.add(ListRow(menuHeader, menuRowAdapter))
            mixedRows.addAll(unitRows)
            mixedRows.add(ListRow(menuHeader, menuRowAdapter))

            // set rows to adapter and ensure correct initial position
            (adapter as ArrayObjectAdapter).setItems(mixedRows, null)
            onRowsLayoutReady(browseData.position)
        }
    }


    private fun updateDirectoryView(browseData: VodDirectoryBrowseLiveData?) {
        val directory = browseData?.directory?: return
        if (browseData.update?.isEmpty() == true) return
        browseData.update?.let {
            when {
                it.isSectionUpdate() -> {
                    loadDirectoryView(browseData)
                }
                it.isUnitUpdate() -> {
                    // update in scope
                    val section = directory.sections[it.sectionIndex!!]
                    ((adapter[it.unitIndex!! + 1] as ListRow).adapter as ArrayObjectAdapter)
                    .setItems(section.units[it.unitIndex!!].window!!.items,null)
                    // check skipped updates
                    for (i in 1 until adapter.size() - 1) {
                        val unit = section.units[i - 1]
                        val rowAdapter = ((adapter[i] as ListRow).adapter as ArrayObjectAdapter)
                        if (unit.window != null && rowAdapter.size() == 0) {
                            rowAdapter.setItems(unit.window!!.items, null)
                        }
                    }
                }
                it.isItemUpdate() -> {
                    ((adapter[it.unitIndex!!] as ListRow).adapter as ArrayObjectAdapter)
                        .replace(it.itemIndex!!,
                            directory.sections[it.sectionIndex!!].units[it.unitIndex!!]
                                .window!!.items[it.itemIndex!!])
                }
            }
        }
    }


    private fun onRowsLayoutReady(initialPosition: VodDirectoryPosition?) {
        view?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // stop observation
                view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                // select initial position
                selectDirectoryPosition(initialPosition)
            }
        })
    }

    private fun selectDirectoryPosition(position: VodDirectoryPosition?) {
        position?.let {
            val setItemPositionTask = if (it.itemIndex > 0) ListRowPresenter.SelectItemViewHolderTask(it.itemIndex) else null
            setItemPositionTask?.isSmoothScroll = false
            setSelectedPosition(it.unitIndex + 1, false, setItemPositionTask)
        }
    }

    class VodUnitListRowAdapter(presenter: Presenter): ArrayObjectAdapter(presenter) {
        override fun getId(position: Int): Long {
            return (get(position) as VodUnit).id
        }
    }

    class VodMenuRowAdapter(presenter: Presenter): ArrayObjectAdapter(presenter) {
        override fun getId(position: Int): Long {
            return (get(position) as CardMenuItem).id
        }
    }

    companion object {
        const val MENU_ITEM_TV_BASE_ID = 1001L
        const val MENU_ITEM_VOD_BASE_ID = 1100L
        const val MENU_ITEM_SETTINGS_ID = 1300L
    }
}