package org.alsi.android.tvlaba.tv.vod.playback

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
import androidx.navigation.fragment.NavHostFragment
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.common.net.HttpHeaders
import dagger.android.support.AndroidSupportInjection
import org.alsi.android.domain.streaming.model.options.VideoAspectRatio
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.framework.VideoLayoutCalculator
import org.alsi.android.presentationtv.model.*
import org.alsi.android.presentationtv.model.TvPlaybackAspectRatioChanged
import org.alsi.android.presentationvod.model.*
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.exception.ClassifiedExceptionHandler
import org.alsi.android.tvlaba.framework.ExoplayerTrackLanguageSelection
import org.alsi.android.tvlaba.framework.TvErrorMessaging
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.tv.directory.TvChannelRowHeaderPresenter
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackAndScheduleFragmentDirections
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackPreferencesDialogFragment
import org.alsi.android.tvlaba.tv.vod.directory.VodItemCardPresenter
import timber.log.Timber
import javax.inject.Inject

class VodPlaybackFragment  : VideoSupportFragment(), Player.Listener, TextOutput {

    @Inject lateinit var viewModelFactory: ViewModelFactory
    @Inject lateinit var errorHandler: ClassifiedExceptionHandler

    private lateinit var playbackViewModel: VodPlaybackViewModel
    private lateinit var preferencesViewModel: VodPlaybackPreferencesViewModel
    private lateinit var footerViewModel : VodPlaybackFooterViewModel

    private lateinit var dataSourceFactory : DefaultDataSourceFactory

    private lateinit var player: SimpleExoPlayer

    private lateinit var glue: VodPlaybackLeanbackGlue

    private lateinit var trackLanguageSelection: ExoplayerTrackLanguageSelection

    private var subtitlesView: SubtitleView? = null

    private lateinit var errorMessaging: TvErrorMessaging

    private var videoLayoutCalculator: VideoLayoutCalculator? = null


    //region Android Interface

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        errorHandler.changeContext(requireActivity())

        playbackViewModel = ViewModelProvider(this, viewModelFactory)
            .get(VodPlaybackViewModel::class.java)

        preferencesViewModel = ViewModelProvider(requireActivity(), viewModelFactory)
            .get(VodPlaybackPreferencesViewModel::class.java)
        trackLanguageSelection = ExoplayerTrackLanguageSelection(requireContext())
        preferencesViewModel.trackLanguageSelection = trackLanguageSelection

        footerViewModel = ViewModelProvider(this, viewModelFactory)
            .get(VodPlaybackFooterViewModel::class.java)

        errorMessaging = TvErrorMessaging(requireContext())

        //setOnItemCardClickedListener()

        dataSourceFactory = DefaultDataSourceFactory(requireContext(), DefaultHttpDataSource.Factory().setUserAgent(
            HttpHeaders.USER_AGENT
        ))

        setupPlayer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup

        subtitlesView = view.findViewById(R.id.leanbackSubtitles)
        addBackPressedCallback()

        val progressView = inflater.inflate(R.layout.progress_view_common, view, false)
        view.addView(progressView)
        progressBarManager.enableProgressBar()
        progressBarManager.setProgressBarView(progressView)

        return view
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
                        // which was popped up out by the attributes
                        navController.navigate(
                            TvPlaybackAndScheduleFragmentDirections
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
        player.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackViewModel.dispose()
        footerViewModel.dispose()
    }

    //endregion
    //region Player Setup

    private fun setupPlayer() {

        player = SimpleExoPlayer.Builder(requireContext())
            .setTrackSelector(trackLanguageSelection.trackSelector).build()
        player.addListener(this)

        val playerAdapter = LeanbackPlayerAdapter(requireContext(),
            player, PLAYER_UPDATE_INTERVAL_MILLIS)

        glue = VodPlaybackLeanbackGlue(requireContext(),
            playerAdapter, playbackViewModel).apply {

            host = VideoSupportFragmentGlueHost(this@VodPlaybackFragment)

            addPlayerCallback(object: PlaybackGlue.PlayerCallback() {

                /**
                 *  ... just to debug playback, remove from production
                 */
                override fun onPlayStateChanged(glue: PlaybackGlue?) {
                    super.onPlayStateChanged(glue)
                    val playerStateName = when(player.playbackState) {
                        Player.STATE_IDLE -> "IDLE"
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY -> "READY"
                        Player.STATE_ENDED -> "ENDED"
                        else -> "UNKNOWN"
                    }
                    Timber.d("@onPlayStateChanged %s", playerStateName)
                }

                override fun onPlayCompleted(glue: PlaybackGlue?) {
                    super.onPlayCompleted(glue)
                    playbackViewModel.onPlayCompleted {
                        // not sure, but does "view?.findNavController()?" get the same navigation controller?
                        val navController = Navigation.findNavController(
                            requireActivity(), R.id.tvGuideNavigationHost)
                        navController.currentDestination?.id?.let {
                            navController.popBackStack(it, true)
                        }
                    }
                }
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
    }

    //endregion
    // region Data Events

    private fun handlePlaybackRequestEvent(resource: Resource<VodPlayback>) {
        when (resource.status) {
            ResourceState.LOADING -> progressBarManager.show()
            ResourceState.SUCCESS -> {
                progressBarManager.hide()
                startPlayback(resource.data)
            }
            ResourceState.ERROR -> {
                progressBarManager.hide()
                errorHandler.run(this, resource.throwable)
            }
        }
    }

    private fun startPlayback(playback: VodPlayback?) {
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

    private fun handlePreferenceChangeEvent(resource: Resource<VodPlaybackPreferenceChangeEvent>) {
        when (resource.status) {
            ResourceState.LOADING -> progressBarManager.show()
            ResourceState.SUCCESS -> {
                progressBarManager.hide()
                when (resource.data) {
                    is VodPlaybackAspectRatioChanged -> setAspectRatio(
                        (resource.data as VodPlaybackAspectRatioChanged).newAspectRatio)
                    is VodPlaybackAudioTrackLanguageChanged -> {}
                    is VodPlaybackSubtitlesLanguageChanged -> {}
                }
            }
            ResourceState.ERROR -> {
                progressBarManager.hide()
                errorHandler.run(this, resource.throwable)
            }
        }
    }

    //endregion
    //region Payer Callbacks

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

    private fun handleFooterDataChange(resource: Resource<VodPlaybackFooterLiveData>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                bindPlaybackFooterData(resource.data)
            }
            ResourceState.LOADING -> {
            }
            ResourceState.ERROR -> {
                errorHandler.run(this, resource.throwable)
            }
        }
    }

    private fun bindPlaybackFooterData(data: VodPlaybackFooterLiveData?) {
        val presenterSelector = ClassPresenterSelector()
            .addClassPresenter(glue.controlsRow::class.java, glue.playbackRowPresenter)
            .addClassPresenter(ListRow::class.java, ListRowPresenter(FocusHighlight.ZOOM_FACTOR_LARGE,true)
                .apply { headerPresenter = TvChannelRowHeaderPresenter() })
        val rowsAdapter = ArrayObjectAdapter(presenterSelector)
        rowsAdapter.add(glue.controlsRow)
        data?.cursor?.unit?.window?.items?.let { items ->
            rowsAdapter.add(ListRow(
                HeaderItem(data.cursor?.unit?.title?: getString(R.string.label_vod_more_in_unit)),
                ArrayObjectAdapter(VodItemCardPresenter()).apply { setItems(items, null) }
            ))
        }
        adapter = rowsAdapter

        // TODO ensure initial selection
        //setOnItemCardSelectedListener()
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