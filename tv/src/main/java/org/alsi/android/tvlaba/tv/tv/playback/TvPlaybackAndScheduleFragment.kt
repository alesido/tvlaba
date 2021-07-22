package org.alsi.android.tvlaba.tv.tv.playback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackGlue
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.Player.STATE_IDLE
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.text.Cue
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.common.net.HttpHeaders.USER_AGENT
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.model.guide.TvWeekDay
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.framework.VideoLayoutCalculator
import org.alsi.android.presentationtv.model.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.databinding.LbPlaybackFragmentBinding
import org.alsi.android.tvlaba.framework.ExoplayerTrackLanguageSelection
import org.alsi.android.tvlaba.framework.TvErrorMessaging
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.tv.schedule.TvScheduleProgramCardPresenter
import org.alsi.android.tvlaba.tv.tv.weekdays.TvWeekDayCardPresenter
import timber.log.Timber
import javax.inject.Inject

class TvPlaybackAndScheduleFragment : VideoSupportFragment(), Player.Listener, TextOutput
{
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var playbackViewModel: TvPlaybackViewModel
    private lateinit var preferencesViewModel: TvPlaybackPreferencesViewModel
    private lateinit var footerViewModel : TvPlaybackFooterViewModel

    private lateinit var dataSourceFactory : DefaultDataSourceFactory

    private lateinit var player: SimpleExoPlayer

    private lateinit var glue: TvPlaybackLeanbackGlue

    private lateinit var trackLanguageSelection: ExoplayerTrackLanguageSelection

    private lateinit var errorMessaging: TvErrorMessaging

    private var _vb: LbPlaybackFragmentBinding? = null
    private val vb get() = _vb!!


    private var videoLayoutCalculator: VideoLayoutCalculator? = null

    // region Android Life Cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        playbackViewModel = ViewModelProvider(this, viewModelFactory)
                .get(TvPlaybackViewModel::class.java)

        preferencesViewModel = ViewModelProvider(requireActivity(), viewModelFactory)
                .get(TvPlaybackPreferencesViewModel::class.java)
        trackLanguageSelection = ExoplayerTrackLanguageSelection(requireContext())
        preferencesViewModel.trackLanguageSelection = trackLanguageSelection

        footerViewModel = ViewModelProvider(this, viewModelFactory)
                .get(TvPlaybackFooterViewModel::class.java)

        errorMessaging = TvErrorMessaging(requireContext())

        setOnItemCardClickedListener()

        dataSourceFactory = DefaultDataSourceFactory(requireContext(), DefaultHttpDataSource.Factory().setUserAgent(USER_AGENT))

        setupPlayer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _vb = LbPlaybackFragmentBinding.inflate(inflater, container, false)
        addBackPressedCallback()
        // FIXME Should return vb.root, otherwise the view binding won't work. Though, there is an exception while doing correctly.
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setupPlayer() {

        player = SimpleExoPlayer.Builder(requireContext())
                .setTrackSelector(trackLanguageSelection.trackSelector)
                .build()

        val playerAdapter = LeanbackPlayerAdapter(
                requireContext(), player, PLAYER_UPDATE_INTERVAL_MILLIS)

        player.addListener(this)

        glue = TvPlaybackLeanbackGlue(requireContext(), playerAdapter, playbackViewModel).apply {

            host = VideoSupportFragmentGlueHost(this@TvPlaybackAndScheduleFragment)

            // add playback state listeners
            addPlayerCallback(object : PlaybackGlue.PlayerCallback() {


                override fun onPreparedStateChanged(glue: PlaybackGlue?) {
                    super.onPreparedStateChanged(glue)

                    // This isn't required to seek to initial playback position here! Looks like
                    // player.seekTo may be called immediately after player started preparation!
                    // See the start playback code.

                    // Place code to do something on a first playback preparation. All subsequent
                    // "next playbacks" won't get here.

                    // See LeanbackPlayerAdapter#maybeNotifyPreparedStateChanged and use general
                    // player events subscription.

//                    if (! isPrepared && isEarlyCompletion()) {
//                        Timber.d("@onPreparedStateChanged onEarlyCompletion")
//                        onEarlyCompletion()
//                    }
                }

                override fun onPlayStateChanged(glue: PlaybackGlue?) {
                    super.onPlayStateChanged(glue)
                    val playerStateName = when(player.playbackState) {
                        STATE_IDLE -> "IDLE"
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY -> "READY"
                        STATE_ENDED -> "ENDED"
                        else -> "UNKNOWN"
                    }
                    Timber.d("@onPlayStateChanged %s", playerStateName)

//                    if ((player.playbackState == STATE_ENDED || player.playbackState == STATE_IDLE)
//                            && isEarlyCompletion()) {
//                        Timber.d("@onPlayStateChanged onEarlyCompletion")
//                        onEarlyCompletion()
//                    }
                }

                override fun onPlayCompleted(glue: PlaybackGlue?) {
                    super.onPlayCompleted(glue)

                    // Test of navigation to outside from nested graph, both methods work
                    // view?.findNavController()?.navigate(R.id.actionGlobalLogOut)

                    playbackViewModel.onPlayCompleted {
                        // not sure, but does "view?.findNavController()?" get the same navigation controller?
                        val navController = Navigation.findNavController(
                                requireActivity(), R.id.tvGuideNavigationHost)
                        navController.currentDestination?.id?.let {
                            navController.popBackStack(it, true)
                        }
                    }
                }

//                private fun isEarlyCompletion(): Boolean {
//                    return !isDetached && !isRemoving
//                            && player.duration < playback?.time?.durationMillis?:0 - 1000L
//                }
            })

            // add navigation to the video preferences fragment
            setPreferencesCallback {
                if (! player.isPlaying) return@setPreferencesCallback
                trackLanguageSelection.update()
                TvPlaybackPreferencesDialogFragment.newInstance().show(childFragmentManager,
                        TvPlaybackPreferencesDialogFragment::class.java.simpleName)
            }

            // start playback or order it to start automatically
            playWhenPrepared()

            // displays the current item's metadata
            playback?.let{ bindPlaybackItem(it) }
        }

        //setPlaybackSeekUiClient(TvPlaybackSeekUiClient())
    }

//    class TvPlaybackSeekUiClient: PlaybackSeekUi.Client() {
//
//        override fun isSeekEnabled() = true
//
//        override fun onSeekStarted() {
//            Timber.d("@onSeekStarted")
//        }
//    }

    private fun addBackPressedCallback() {
        val navController = findNavController(this)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object:
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    remove() // remove this listener

                    if (null == navController.previousBackStackEntry) {
                        // previous destination was the start fragment of the navigation graph,
                        // which was popped up out by the attributes
                        navController.navigate(TvPlaybackAndScheduleFragmentDirections
                            .actionTvPlaybackAndScheduleFragmentToTvProgramDetailsFragment())
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

        playbackViewModel.getLiveData().observe(this, {
            if (it != null) handlePlaybackRequestEvent(it)
        })
        preferencesViewModel.getPreferenceChangeLiveData().observe(requireActivity(), {
            if (it != null) handlePreferenceChangeEvent(it)
        })
        footerViewModel.getLiveData().observe(this, {
            if (it != null) handleFooterDataChange(it)
        })
    }

    override fun onPause() {
        super.onPause()
        playbackViewModel.recordPlaybackState(player.currentPosition)
        player.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackViewModel.dispose()
        footerViewModel.dispose()
    }

    // endregion
    // region Data Events

    private fun handlePlaybackRequestEvent(resource: Resource<TvPlayback>) {
        when (resource.status) {
            ResourceState.SUCCESS -> startPlayback(resource.data)
            ResourceState.LOADING -> {
            }
            ResourceState.ERROR -> {
                Toast.makeText(context, errorMessaging.m(resource), Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    private fun startPlayback(playback: TvPlayback?) {
        playback?.stream?.uri?: return
        context?.let {
            Timber.d("@startPlayback %s @ %d", playback.title, playback.position)
            if (glue.bindPlaybackItem(playback)) {

                // reset player (seems not required, but gives better look by resetting
                // progress view immediately)
                if (player.isPlaying) {
                    Timber.d("@startPlayback stop & reset %s", playback.title)
                    player.stop()
                }

                // update program data display
                adapter.notifyItemRangeChanged(0, 1)

                // create media source
                val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(playback.stream!!.uri.toString()))

                // start preparation
                //player.prepare(hlsMediaSource, true, true) - deprecated
                player.setMediaSource(hlsMediaSource, true)

                // request initial position (tested, works)
                player.seekTo(playback.position)

                player.prepare()
            }
            else {
                Toast.makeText(context, R.string.error_message_no_playback_available, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handlePreferenceChangeEvent(resource: Resource<PlaybackPreferenceChangeEvent>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                when (resource.data) {
                    is PlaybackAspectRatioChanged -> setAspectRatio(
                            (resource.data as PlaybackAspectRatioChanged).newAspectRatio)
                    is PlaybackAudioTrackLanguageChanged -> {

                    }
                    is PlaybackSubtitlesLanguageChanged -> {

                    }
                }
            }
            ResourceState.LOADING -> {
            }
            ResourceState.ERROR -> {
                Toast.makeText(context, errorMessaging.m(resource), Toast.LENGTH_LONG).show()
            }
            else -> {
            }
        }
    }

    // region Payer Callbacks

    override fun onVideoSizeChanged(width: Int, height: Int) {
        super<VideoSupportFragment>.onVideoSizeChanged(width, height)
        videoLayoutCalculator = VideoLayoutCalculator(requireContext(), width, height)
    }

    private fun setAspectRatio(targetAspectRatio: VideoAspectRatio) {
        videoLayoutCalculator?: return
        surfaceView?: return
        val t = videoLayoutCalculator!!.calculate(targetAspectRatio)
        val p = surfaceView.layoutParams
        p.width = t.width
        p.height = t.height
        surfaceView.layoutParams = p
    }


    override fun onError(errorCode: Int, errorMessage: CharSequence?) {
        super.onError(errorCode, errorMessage)
    }

    // endregion
    // region Footer Data

    private fun handleFooterDataChange(resource: Resource<TvPlaybackFooterLiveData>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                bindPlaybackFooterData(resource.data)
            }
            ResourceState.LOADING -> {
            }
            ResourceState.ERROR -> {
                Toast.makeText(context, errorMessaging.m(resource), Toast.LENGTH_LONG).show()
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
    private fun bindPlaybackFooterData(data: TvPlaybackFooterLiveData?) {
        val listRowPresenter = ListRowPresenter()
        val presenterSelector = ClassPresenterSelector()
                .addClassPresenter(glue.controlsRow::class.java, glue.playbackRowPresenter)
                .addClassPresenter(ListRow::class.java, listRowPresenter)
        val rowsAdapter = ArrayObjectAdapter(presenterSelector)
        rowsAdapter.add(glue.controlsRow)
        data?.schedule?.let {
            rowsAdapter.add(ListRow(
                    HeaderItem(getString(R.string.header_day_schedule, it.longDateString)),
                    ArrayObjectAdapter(TvScheduleProgramCardPresenter())
                            .apply { setItems(it.items, null) }))
        }
        data?.weekDayRange?.let {
            val listRowAdapter = ArrayObjectAdapter(TvWeekDayCardPresenter()).apply {
                setItems(it.weekDays, null)
            }
            rowsAdapter.add(ListRow(listRowAdapter))
        }
        adapter = rowsAdapter

        // ensure initial schedule & week day selection
        setOnItemCardSelectedListener()
    }

    // endregion
    // region Item Action Listeners

    private fun setOnItemCardSelectedListener() {
        var isInitialProgramSelection = true
        var isInitialWeekDaySelection = true

        setOnItemViewSelectedListener { _, item, rowViewHolder, _ ->
            if (isInitialProgramSelection && item is TvProgramIssue) {
                val gridView = (rowViewHolder as ListRowPresenter.ViewHolder).gridView
                if (footerViewModel.currentScheduleItemPosition
                        != footerViewModel.scheduleItemPositionOf(item))
                    gridView.selectedPosition = footerViewModel.currentScheduleItemPosition
                isInitialProgramSelection = false

            }
            if (isInitialWeekDaySelection && item is TvWeekDay) {
                val gridView = (rowViewHolder as ListRowPresenter.ViewHolder).gridView
                if (footerViewModel.selectedWeekDayPosition != footerViewModel.weekDayPositionOf(item))
                    gridView.selectedPosition = footerViewModel.selectedWeekDayPosition
                isInitialWeekDaySelection = false
            }
        }
    }

    private fun setOnItemCardClickedListener() {
        setOnItemViewClickedListener { _, item, _, _ ->
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
    // Subtitles

    override fun onCues(cues: MutableList<Cue>) {
        vb.leanbackSubtitles.setCues(cues)
    }

    // endregion
    // Companion Object

    companion object {
        /**
         * How often the player refreshes its views, in milliseconds.
         **/
        private const val PLAYER_UPDATE_INTERVAL_MILLIS: Int = 100
    }

    // endregion
}