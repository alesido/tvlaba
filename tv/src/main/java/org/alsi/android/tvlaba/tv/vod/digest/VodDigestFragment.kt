package org.alsi.android.tvlaba.tv.vod.digest

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationvod.model.VodDigestLiveData
import org.alsi.android.presentationvod.model.VodDigestUpdateScope
import org.alsi.android.presentationvod.model.VodDigestViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.tv.program.TvProgramDetailsFragment
import org.alsi.android.tvlaba.tv.vod.directory.VodItemCardPresenter
import javax.inject.Inject

class VodDigestFragment : DetailsSupportFragment() {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var digestViewModel: VodDigestViewModel

    private val bgController = DetailsSupportFragmentBackgroundController(this)

    private val listingRowPosition get() = adapter.size() - 1

    private var initialRow = RowKind.DETAILS


    private var isNavigatedBack = false

    // region Life Cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        val progressView = inflater.inflate(R.layout.progress_view_common, view, false)
        view.addView(progressView)
        progressBarManager.enableProgressBar()
        progressBarManager.setProgressBarView(progressView)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBackPressedCallback()
    }

    private fun addBackPressedCallback() {
        val navController = NavHostFragment.findNavController(this)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object:
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    remove() // remove this listener
                    if (null == navController.previousBackStackEntry) {
                        // previous destination was the start fragment of the navigation graph,
                        // which was popped up out by the attributes - navigate back manually:
                        navController.navigate(
                            VodDigestFragmentDirections
                                .actionVodDigestFragmentToVodDirectoryFragment())
                    }
                    else {
                        requireActivity().onBackPressed()
                    }
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        launchViewModel()
    }

    override fun onResume() {
        super.onResume()
        if (isNavigatedBack)
            digestViewModel.onBackNavigation()
        else
            isNavigatedBack = true
    }

    override fun onDestroy() {
        super.onDestroy()
        digestViewModel.dispose()
    }

    //endregion
    //region View Model

    private fun setupViewModel() {
        digestViewModel = ViewModelProvider(this, viewModelFactory)
            .get(VodDigestViewModel::class.java)
    }

    private fun launchViewModel() {
        digestViewModel.getLiveData().observe(this, {
            if (it != null) handleDetailsChangeEvent(it)
        })
    }

    private fun handleDetailsChangeEvent(resource: Resource<VodDigestLiveData>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                progressBarManager.hide()
                when (resource.data?.updateScope) {
                    VodDigestUpdateScope.DIGEST -> updateAll(resource.data)
                    VodDigestUpdateScope.LISTING -> updateListingOnly(resource.data)
                }
            }
            ResourceState.LOADING -> progressBarManager.show()
            ResourceState.ERROR -> {
                progressBarManager.hide()
                errorHandler.run(this, resource.throwable)
            }
            else -> {
                progressBarManager.hide()
            }
        }
    }

    //endregion
    //region Adapter

    private fun setupAdapter() {

        // details presenter
        val detailsPresenter = createDetailsPresenter()

        // presenter selector
        val rowPresenterSelector = ClassPresenterSelector()
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
        rowPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())

        // adapter
        adapter = ArrayObjectAdapter(rowPresenterSelector)

        // --
        setOnItemCardClickedListener()
    }

    private fun createDetailsPresenter(): FullWidthDetailsOverviewRowPresenter {
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(
            VodItemDescriptionPresenter(requireContext()),
            VodItemDetailsPosterSimplePresenter()
        )

        detailsPresenter.initialState = FullWidthDetailsOverviewRowPresenter.STATE_HALF
        detailsPresenter.backgroundColor = ContextCompat.getColor(
            requireActivity(), R.color.lb_default_brand_color)

        // actions
        setOnActionListener(detailsPresenter)

        // entrance transition
        setupEntranceTransition(detailsPresenter)

        return detailsPresenter
    }


    private fun setOnItemCardSelectedActions() {
        var doSelectInitialListingPosition = true
        setOnItemViewSelectedListener { _, item, rowViewHolder, _ ->
            when (item) {
                is VodListingItem -> {
                    val gridView = (rowViewHolder as ListRowPresenter.ViewHolder).gridView
                    if (doSelectInitialListingPosition) {
                        gridView.selectedPosition = digestViewModel.currentListingPosition
                        doSelectInitialListingPosition = false
                    }
                    digestViewModel.onListingItemSelected(item, gridView.selectedPosition)
                }
            }
        }
    }

    private fun setOnItemCardClickedListener() {
        setOnItemViewClickedListener { _, item, rowViewHolder, _ ->
            when (item) {
                is VodListingItem -> {
                    digestViewModel.onListingItemAction(item,
                        (rowViewHolder as ListRowPresenter.ViewHolder)
                            .gridView.selectedPosition)
                }
            }
        }
    }

    private fun setupEntranceTransition(detailsPresenter: FullWidthDetailsOverviewRowPresenter) {
        val transitionHelper = FullWidthDetailsOverviewSharedElementHelper()
        transitionHelper.setSharedElementEnterTransition(requireActivity(),
            TvProgramDetailsFragment.SHARED_ELEMENT_NAME
        )
        detailsPresenter.setListener(transitionHelper)
        detailsPresenter.isParticipatingEntranceTransition = false
        prepareEntranceTransition()
    }

    //region Data Binding

    private fun updateAll(data: VodDigestLiveData?) {
        // preconditions
        val item = data?.details
        if (null == item) {
            Toast.makeText(context, R.string.error_message_no_program_data_available,
                Toast.LENGTH_LONG).show()
            NavHostFragment.findNavController(this).popBackStack()
            return
        }
        // details "row"
        val detailsRow = DetailsOverviewRow(data)
        val options: RequestOptions = RequestOptions().error(R.drawable.default_background)

        // uris
        val posterUri =  item.posters?.poster?: if (item.posters?.gallery?.isNotEmpty() == true)
            item.posters!!.gallery!!.elementAt(0) else null

        val backgroundUri = item.posters?.gallery?.let { uris ->
            if (uris.size > 2) uris[1] else uris[0] } ?: posterUri

        // poster
        Glide.with(this@VodDigestFragment).asBitmap().load(posterUri?.toString())
            .apply(options).into(PosterBitmapTarget(detailsRow))

        // background
        bgController.coverBitmap = null
        bgController.enableParallax()
        Glide.with(this).asBitmap().load(backgroundUri?.toString())
            .apply(options).into(BackgroundBitmapTarget())

        // actions
        setupActions(item, detailsRow)
        setOnItemCardSelectedActions()

        // adapter rows
        with(adapter as ArrayObjectAdapter) {
            removeItems(0, size())
            add(detailsRow)
            vodUnitListingRow(data)?.let { add(it) }
        }
    }

    private fun updateListingOnly(data: VodDigestLiveData?) {
        val items = data?.cursor?.unit?.window?.items?: return
        ((adapter[listingRowPosition] as ListRow).adapter as ArrayObjectAdapter)
            .setItems(items, null)
    }

    inner class PosterBitmapTarget(private val row : DetailsOverviewRow): CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
            row.setImageBitmap(requireActivity(), resource)
            startEntranceTransition()
        }
        override fun onLoadCleared(placeholder: Drawable?) {
            // if the bitmap referenced somewhere else too other than this imageView, clear it here as you can no longer have the bitmap
        }
    }

    inner class BackgroundBitmapTarget(): CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
            bgController.coverBitmap = resource
        }
        override fun onLoadCleared(placeholder: Drawable?) {
            // if the bitmap referenced somewhere else too other than this imageView, clear it here as you can no longer have the bitmap
        }
    }

    private fun vodUnitListingRow(data: VodDigestLiveData?): ListRow? {
        val items = data?.cursor?.unit?.window?.items?: return null
        if (items.isEmpty()) return null
        return ListRow(
            HeaderItem(data.cursor?.unit?.title?: getString(R.string.label_vod_more_in_unit)),
            ArrayObjectAdapter(VodItemCardPresenter()).apply {
                setItems(items, null)
            }
        )
    }

    //endregion
    //region Actions

    private fun setupActions(item: VodListingItem, detailsOverviewRow: DetailsOverviewRow) {
        val actionsAdapter = SparseArrayObjectAdapter()

        actionsAdapter[ACTION_PLAY] = Action(ACTION_PLAY.toLong(), getString(R.string.vod_digest_action_watch))
        actionsAdapter[ACTION_NEXT] = Action(ACTION_NEXT.toLong(), getString(R.string.vod_digest_action_next))
        actionsAdapter[ACTION_NEXT] = Action(ACTION_LISTING.toLong(), getString(R.string.vod_digest_action_listing))

        detailsOverviewRow.actionsAdapter = actionsAdapter
    }

    private fun setOnActionListener(detailsPresenter: FullWidthDetailsOverviewRowPresenter) {
        detailsPresenter.setOnActionClickedListener {
            when(it.id.toInt()) {
                ACTION_PLAY -> {
                    digestViewModel.onPlaybackAction() {
                        Navigation.findNavController(requireActivity(), R.id.tvGuideNavigationHost)
                            .navigate(
                                VodDigestFragmentDirections
                                .actionVodDigestFragmentToVodPlaybackFragment())
                    }
                }
                ACTION_NEXT -> {
                }
                ACTION_LISTING -> {
                    setSelectedPosition(listingRowPosition)
                    ((adapter[listingRowPosition] as ListRow).adapter as ArrayObjectAdapter)

                }
            }
        }
    }

    //endregion
    //region Constants

    private enum class RowKind {
        DETAILS, FOOTAGE, CREDITS, LISTING
    }

    companion object {
        const val SHARED_ELEMENT_NAME = "hero"

        const val ACTION_PLAY = 1
        const val ACTION_NEXT = 2
        const val ACTION_LISTING = 3
    }

    //endregion
}