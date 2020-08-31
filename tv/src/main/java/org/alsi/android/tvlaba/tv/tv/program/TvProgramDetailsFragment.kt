package org.alsi.android.tvlaba.tv.tv.program

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.model.guide.TvWeekDay
import org.alsi.android.domain.tv.model.guide.TvWeekDayRange
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvProgramDetailsLiveData
import org.alsi.android.presentationtv.model.TvProgramDetailsViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.tv.schedule.TvScheduleProgramCardPresenter
import org.alsi.android.tvlaba.tv.tv.weekdays.TvWeekDayCardPresenter
import javax.inject.Inject

class TvProgramDetailsFragment : DetailsSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var detailsViewModel: TvProgramDetailsViewModel

    private val scheduleRowPosition get() = adapter.size() - 2

    private var initialRow = RowKind.DETAILS

    // region Life Cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupAdapter()
    }

    override fun onStart() {
        super.onStart()
        launchViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        detailsViewModel.dispose()
    }

    // endregion
    // region View Model setup

    private fun setupViewModel() {
        detailsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvProgramDetailsViewModel::class.java)
    }

    private fun launchViewModel() {
        detailsViewModel.getLiveData().observe(this,
                Observer<Resource<TvProgramDetailsLiveData>> {
                    if (it != null) handleDetailsChangeEvent(it)
                })
    }

    private fun handleDetailsChangeEvent(resource: Resource<TvProgramDetailsLiveData>) {
        when (resource.status) {
            ResourceState.SUCCESS -> bindProgramData(resource.data)
            ResourceState.LOADING -> {}
            ResourceState.ERROR -> {
                Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    //endregion
    // region Adapter Setup

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
                TvProgramDetailsDescriptionPresenter(requireContext()),
                TvProgramDetailsPosterSimplePresenter()
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

    private fun setupActions(detailsOverviewRow: DetailsOverviewRow) {
        val actionsAdapter = SparseArrayObjectAdapter()
        actionsAdapter[ACTION_WATCH] = Action(ACTION_WATCH.toLong(),
                resources.getString(R.string.tv_program_details_action_watch))
        actionsAdapter[ACTION_SCHEDULE] = Action(ACTION_SCHEDULE.toLong(),
                resources.getString(R.string.tv_program_details_action_schedule))
        detailsOverviewRow.actionsAdapter = actionsAdapter
    }

    private fun setOnActionListener(detailsPresenter: FullWidthDetailsOverviewRowPresenter) {
        detailsPresenter.setOnActionClickedListener {
            when(it.id.toInt()) {
                ACTION_WATCH -> {
                    detailsViewModel.onPlaybackAction() {
                        Navigation.findNavController(requireActivity(), R.id.tvGuideNavigationHost)
                                .navigate(TvProgramDetailsFragmentDirections
                                        .actionTvProgramDetailsFragmentToTvPlaybackAndScheduleFragment())
                    }
                }
                ACTION_SCHEDULE -> {
                    setSelectedPosition(scheduleRowPosition)
                }
            }
        }
    }

    private fun setOnItemCardSelectedActions() {
        var isInitialProgramSelection = true
        var isInitialWeekDaySelection = true
        setOnItemViewSelectedListener { _, item, rowViewHolder, _ ->
            if (isInitialProgramSelection && item is TvProgramIssue) {
                val gridView = (rowViewHolder as ListRowPresenter.ViewHolder).gridView
                if (detailsViewModel.currentScheduleItemPosition
                        != detailsViewModel.scheduleItemPositionOf(item))
                    gridView.selectedPosition = detailsViewModel.currentScheduleItemPosition
                isInitialProgramSelection = false

            }
            if (isInitialWeekDaySelection && item is TvWeekDay) {
                val gridView = (rowViewHolder as ListRowPresenter.ViewHolder).gridView
                if (detailsViewModel.selectedWeekDayPosition != detailsViewModel.weekDayPositionOf(item))
                    gridView.selectedPosition = detailsViewModel.selectedWeekDayPosition
                isInitialWeekDaySelection = false
            }
        }
    }

    private fun setOnItemCardClickedListener() {
        setOnItemViewClickedListener { _, item, _, _ ->
           when (item) {
               is TvProgramIssue -> {
                   detailsViewModel.onTvProgramIssueAction(item)
               }
               is TvWeekDay -> {
                   detailsViewModel.onWeekDayAction(item)
                   initialRow = RowKind.SCHEDULE
               }
           }
        }
    }

    private fun setupEntranceTransition(detailsPresenter: FullWidthDetailsOverviewRowPresenter) {
        val transitionHelper = FullWidthDetailsOverviewSharedElementHelper()
        transitionHelper.setSharedElementEnterTransition(requireActivity(), SHARED_ELEMENT_NAME)
        detailsPresenter.setListener(transitionHelper)
        detailsPresenter.isParticipatingEntranceTransition = false
        prepareEntranceTransition()
    }

    // endregion
    // region Data Binding

    private fun bindProgramData(data: TvProgramDetailsLiveData?) {

        val program = data?.cursor?.program

        if (null == program) {
            Toast.makeText(context, R.string.error_message_no_program_data_available,
                    Toast.LENGTH_LONG).show()
            Navigation.findNavController(requireActivity(), R.id.tvGuideNavigationHost).popBackStack()
            return
        }

        val detailsRow = DetailsOverviewRow(data)

        val options: RequestOptions = RequestOptions()
                .error(R.drawable.default_background).dontAnimate()
        program.let {
            Glide.with(this).asBitmap().load(it.mainPosterUri.toString()).apply(options)
                    .into(PosterBitmapTarget(detailsRow))
        }

        setupActions(detailsRow)
        setOnItemCardSelectedActions()

        with(adapter as ArrayObjectAdapter) {
            removeItems(0, size())
            add(detailsRow)
            postersRow(program)?.let { add(it) }
            actorsRow(program)?.let { add(it) }
            scheduleRow(data.cursor?.schedule)?.let { add(it) }
            weekDayRow(data.weekDayRange)?.let { add(it) }
//            add(navigationRow())
        }

        if (initialRow == RowKind.SCHEDULE) {
            setSelectedPosition(scheduleRowPosition)
            initialRow = RowKind.DETAILS
        }
    }

    private fun postersRow(program: TvProgramIssue): ListRow? {
        if (program.allPosterUris.isNullOrEmpty()) return null
        return ListRow(
                HeaderItem(getString(R.string.header_footage)),
                ArrayObjectAdapter(VideoPosterCardPresenter()).apply {
                    setItems(program.allPosterUris?.map { Uri.parse(it.toString()) }, null)
                })
    }

    private fun actorsRow(program: TvProgramIssue): ListRow? {
        if (program.credits.isNullOrEmpty()) return null
        return ListRow(
                HeaderItem(getString(R.string.header_credits)),
                ArrayObjectAdapter(TvProgramCreditsCardPresenter()).apply {
                    setItems(program.creditPictures, null)
                })
    }

    private fun scheduleRow(schedule: TvDaySchedule?): ListRow? {
        if (null == schedule || schedule.items.isNullOrEmpty()) return null
        return ListRow(
                HeaderItem(getString(R.string.header_day_schedule, schedule.longDateString)),
                ArrayObjectAdapter(TvScheduleProgramCardPresenter()).apply {
                    setItems(schedule.items, null)
                })
    }

    private fun weekDayRow(weekDayRange: TvWeekDayRange?): ListRow? {
        if (null == weekDayRange || weekDayRange.weekDays.isNullOrEmpty()) return null
        return ListRow(
                ArrayObjectAdapter(TvWeekDayCardPresenter()).apply {
                    setItems(weekDayRange.weekDays, null)
                })
    }

//    private fun navigationRow(): ListRow {
//
//    }

    inner class PosterBitmapTarget(private val row : DetailsOverviewRow): CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
            row.setImageBitmap(requireActivity(), resource)
            startEntranceTransition()
        }
        override fun onLoadCleared(placeholder: Drawable?) {
            // if the bitmap referenced somewhere else too other than this imageView
            // clear it here as you can no longer have the bitmap
        }
    }

    // endregion

    private enum class RowKind {
        DETAILS, FOOTAGE, CREDITS, SCHEDULE, WEEKDAYS, NAVIGATION
    }

    companion object {
        const val SHARED_ELEMENT_NAME = "hero"

        const val ACTION_WATCH = 1
        const val ACTION_SCHEDULE = 2
    }
}

