package org.alsi.android.tvlaba.tv.tv

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackGlue
import androidx.leanback.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.model.guide.TvWeekDay
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvPlaybackFooterLiveData
import org.alsi.android.presentationtv.model.TvPlaybackViewModel
import org.alsi.android.presentationtv.model.TvPlaybackFooterViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.tv.directory.TvScheduleProgramCardPresenter
import org.alsi.android.tvlaba.tv.tv.directory.TvWeekDayCardPresenter
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackLeanbackGlue
import javax.inject.Inject

class TvPlaybackAndScheduleFragment : VideoSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var playbackViewModel: TvPlaybackViewModel

    private lateinit var footerViewModel : TvPlaybackFooterViewModel

    private lateinit var player: SimpleExoPlayer

    private lateinit var glue: TvPlaybackLeanbackGlue

    private var playback: TvPlayback? = null

    // region Android Life Cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        playbackViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvPlaybackViewModel::class.java)

        footerViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvPlaybackFooterViewModel::class.java)

        setOnItemViewClickedListener(ItemClickListener())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        player = SimpleExoPlayer.Builder(context).build()

        val playerAdapter = LeanbackPlayerAdapter(
                requireContext(), player, PLAYER_UPDATE_INTERVAL_MILLIS)

        glue = TvPlaybackLeanbackGlue(context, playerAdapter).apply {

            host = VideoSupportFragmentGlueHost(this@TvPlaybackAndScheduleFragment)

            // add playback state listeners
            addPlayerCallback(object : PlaybackGlue.PlayerCallback() {

                override fun onPreparedStateChanged(glue: PlaybackGlue?) {
                    super.onPreparedStateChanged(glue)
                    if (glue?.isPrepared == true) {
                        playback?.let {
                            seekTo(it.position)
                        }
                    }
                }

                override fun onPlayCompleted(glue: PlaybackGlue?) {
                    super.onPlayCompleted(glue)
                    val navController = Navigation.findNavController(
                            requireActivity(), R.id.tvGuideNavigationHost)
                    navController.currentDestination?.id?.let {
                        navController.popBackStack(it, true)
                    }
                }
            })

            // start playback or order it to start automatically
            playWhenPrepared()

            // displays the current item's metadata
            playback?.let{ bindPlaybackItem(it) }
        }
    }

    override fun onPause() {
        super.onPause()
        player.release()
    }

    override fun onStart() {
        super.onStart()
        playbackViewModel.getLiveData().observe(this,
                Observer<Resource<TvPlayback>> {
                    if (it != null) handlePlaybackRequestEvent(it)
                })
        footerViewModel.getLiveData().observe(this,
                Observer<Resource<TvPlaybackFooterLiveData>> {
                    if (it != null) handleFooterDataChange(it)
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackViewModel.dispose()
        footerViewModel.dispose()
    }

    // endregion
    // region

    private fun handlePlaybackRequestEvent(resource: Resource<TvPlayback>) {
        when (resource.status) {
            ResourceState.SUCCESS -> startPlayback(resource.data)
            ResourceState.LOADING -> {}
            ResourceState.ERROR -> {
                Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    private fun startPlayback(playback: TvPlayback?) {
        if (null == playback?.streamUri) return
        context?.let {
            if (glue.bindPlaybackItem(playback)) {
                val dataSourceFactory = DefaultHttpDataSourceFactory(
                        Util.getUserAgent(it, getString(R.string.app_name)))
                val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(playback.streamUri.toString()))
                player.prepare(hlsMediaSource, false, true)
            }
            else {
                Toast.makeText(context, R.string.error_message_no_playback_available, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleFooterDataChange(resource: Resource<TvPlaybackFooterLiveData>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                refreshPlaybackFooter(resource.data)
            }
            ResourceState.LOADING -> {
            }
            ResourceState.ERROR -> {
                Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
            }
            else -> {
            }
        }
    }

    /** Add program schedule rows, week day selector stripe below the player controls.
     *
     * To add a new row to the player adapter and not lose the controls row that is provided by the
     * glue, we need to compose a new row with the controls row and schedule row.
     *
     * We start by creating a new {@link ClassPresenterSelector} which, yes, can select
     * an appropriate presenter for a presented row by its class as a key.
     *
     * Then add the controls row from the media player glue, then add the schedule rows.
     *
     * @see "tv-samples/Leanback sample"
     */
    private fun refreshPlaybackFooter(data: TvPlaybackFooterLiveData?) {
        val listRowPresenter = ListRowPresenter()
        val presenterSelector = ClassPresenterSelector()
                .addClassPresenter(glue.controlsRow::class.java, glue.playbackRowPresenter)
                .addClassPresenter(ListRow::class.java, listRowPresenter)
        val rowsAdapter = ArrayObjectAdapter(presenterSelector)
        rowsAdapter.add(glue.controlsRow)
        data?.schedule?.let {
            it.sections.mapIndexed { i, section ->
                val header = HeaderItem(i.toLong(), section.title)
                val listRowAdapter = ArrayObjectAdapter(TvScheduleProgramCardPresenter()).apply {
                    setItems(section.items, null)
                }
                val row = ListRow(header, listRowAdapter)
                rowsAdapter.add(row)
            }
        }
        data?.weekDayRange?.let {
            val listRowAdapter = ArrayObjectAdapter(TvWeekDayCardPresenter()).apply {
                setItems(it.weekDays, null)
            }
            rowsAdapter.add(ListRow(listRowAdapter))
        }
        adapter = rowsAdapter

        // ensure initial week day selection
        var isInitialSelection = true
        setOnItemViewSelectedListener { _, item, rowViewHolder, _ ->
            if (isInitialSelection && item is TvWeekDay) {
                val gridView = (rowViewHolder as ListRowPresenter.ViewHolder).gridView
                if (footerViewModel.selectedWeekDayPosition != footerViewModel.weekDayPositionOf(item))
                    gridView.selectedPosition = footerViewModel.selectedWeekDayPosition
                isInitialSelection = false
            }
        }
    }

    private inner class ItemClickListener: OnItemViewClickedListener {
        override fun onItemClicked(
                itemViewHolder: Presenter.ViewHolder?,
                item: Any?,
                rowViewHolder: RowPresenter.ViewHolder?,
                row: Row?
        ) {
            when (item) {
                is TvProgramIssue -> {
                    hideControlsOverlay(true)
                    this@TvPlaybackAndScheduleFragment.playbackViewModel.onTvProgramIssueAction(item)
                }
                is TvWeekDay -> {
                    this@TvPlaybackAndScheduleFragment.footerViewModel.onTvWeekDayAction(item)
                }
            }
        }
    }

    // endregion

    companion object {
        private val TAG = TvPlaybackAndScheduleFragment::class.java.simpleName

        /** How often the player refreshes its views in milliseconds */
        private const val PLAYER_UPDATE_INTERVAL_MILLIS: Int = 100

        /** Time between metadata updates in milliseconds */
        private val METADATA_UPDATE_INTERVAL_MILLIS: Long = java.util.concurrent.TimeUnit.SECONDS.toMillis(10)

        /** Default time used when skipping playback in milliseconds */
        private val SKIP_PLAYBACK_MILLIS: Long = java.util.concurrent.TimeUnit.SECONDS.toMillis(10)
    }
}