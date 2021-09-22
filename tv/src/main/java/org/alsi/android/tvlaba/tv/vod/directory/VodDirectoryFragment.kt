package org.alsi.android.tvlaba.tv.vod.directory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection
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

        val progressView = inflater.inflate(R.layout.progress_view_common, view, false)
        view.addView(progressView)

        progressBarManager.enableProgressBar()
        progressBarManager.setProgressBarView(progressView)

        return view
    }

    private fun setSelectedListener() {

        setOnItemViewSelectedListener { _, item, rowViewHolder, row ->
            // NOTE If item is null, rowViewHolder can be
            if (null == item && row != null) {
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
//                    else {
//                        // TODO navigate by menu item ID
//                    }
                }
                is VodUnit -> browseViewModel.onUnitSelected(item)
                is VodListingItem -> browseViewModel.onListingItemSelected(item,
                    (rowViewHolder as ListRowPresenter.ViewHolder).gridView.selectedPosition)
            }
        }
    }

    private fun setClickedListener() {

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
            // row: sections + menu
            val menuHeader = HeaderItem(0L, getString(R.string.label_vod_sections_and_navigation))
            val menuItems: MutableList<CardMenuItem> = mutableListOf()
            // .. sections
            if (directory.sections.size > 1 || directory.sections[0].isSectionSubstitute != true) {
                menuItems.addAll(directory.sections.mapIndexed { index, section ->
                    CardMenuItem(index.toLong(), section.title, payload = section)
                })
            }
            // .. menu
            menuItems.add(CardMenuItem(MENU_ITEM_TV_ID, "TV"))
            menuItems.add(CardMenuItem(MENU_ITEM_SETTINGS, "SETTINGS"))
            val menuRowAdapter = VodMenuRowAdapter(VodMenuCardPresenter()).apply {
                setItems(menuItems, null)
            }

            // units
            val unitRows = directory.sections[browseData.position.sectionIndex].units.mapIndexed { index, unit ->
                val header = HeaderItem((index + 1).toLong(), unit.title)
                val listRowAdapter = VodUnitListRowAdapter(VodItemCardPresenter()).apply {
                    setItems(unit.window?.items?: listOf<VodListingItem>(), null)
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
        const val MENU_ITEM_TV_ID = 1001L
        const val MENU_ITEM_SETTINGS = 1002L
    }
}