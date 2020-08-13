package org.alsi.android.tvlaba.tv.tv

import android.content.Context
import android.net.Uri
import android.os.Bundle
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
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvChannelDirectoryBrowseViewModel
import org.alsi.android.presentationtv.model.TvPlaybackViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.tv.directory.TvDirectoryChannelCardPresenter
import org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackLeanbackGlue
import javax.inject.Inject

class TvPlaybackAndScheduleFragment : VideoSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var playbackViewModel: TvPlaybackViewModel

    private lateinit var scheduleViewModel : TvChannelDirectoryBrowseViewModel

    private lateinit var player: SimpleExoPlayer

    private lateinit var glue: TvPlaybackLeanbackGlue

    private var playback: TvPlayback? = null

    // region Android Life Cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        playbackViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvPlaybackViewModel::class.java)

        scheduleViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvChannelDirectoryBrowseViewModel::class.java)
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
            playback?.let{ setMetadata(it) }
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
        scheduleViewModel.getLiveData().observe(this,
                Observer<Resource<TvChannelDirectory>> {
                    if (it != null) handleCategoriesListDataState(it)
                })

    }

    override fun onDestroy() {
        super.onDestroy()
        playbackViewModel.dispose()
    }

    // endregion
    // region

    private fun handlePlaybackRequestEvent(resource: Resource<TvPlayback>) {
        when (resource.status) {
            ResourceState.SUCCESS -> startPlayback(resource.data)
            ResourceState.LOADING -> {}
            ResourceState.ERROR -> {}
            else -> {}
        }
    }

    private fun startPlayback(playback: TvPlayback?) {
        if (null == playback?.streamUri)
            return

        context?.let {
            glue.setMetadata(playback)

            val t = playback.time
            if (t != null) {
                glue.overrideDuration(t.endUnixTimeMillis - t.startUnixTimeMillis)
                glue.maintainLivePosition = true
            }

            val dataSourceFactory = DefaultHttpDataSourceFactory(
                    Util.getUserAgent(it, getString(R.string.app_name)))
            val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(playback.streamUri.toString()))

            player.prepare(hlsMediaSource, false, true)
        }
    }

    private fun handleCategoriesListDataState(resource: Resource<TvChannelDirectory>) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                addScheduleRows(resource.data)
            }
            ResourceState.LOADING -> {
            }
            ResourceState.ERROR -> {
            }
            else -> {
            }
        }
    }

    /** Add program schedule rows below the player controls.
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
    private fun addScheduleRows(directory: TvChannelDirectory?) {
        val presenterSelector = ClassPresenterSelector()
                .addClassPresenter(glue.controlsRow::class.java, glue.playbackRowPresenter)
                .addClassPresenter(ListRow::class.java, ListRowPresenter())
        val rowsAdapter = ArrayObjectAdapter(presenterSelector)
        rowsAdapter.add(glue.controlsRow)
        directory?.let {
            //val categoryRows =
            directory.categories.mapIndexed { idx, category ->
                val header = HeaderItem(idx.toLong(), category.title)
                val listRowAdapter = ArrayObjectAdapter(TvDirectoryChannelCardPresenter()).apply {
                    setItems(directory.index[category.id], null)
                }
                val row = ListRow(header, listRowAdapter)
                rowsAdapter.add(row)
            }
        }
        adapter = rowsAdapter
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