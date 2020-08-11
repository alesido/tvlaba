package org.alsi.android.tvlaba.tv.tv

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackGlue
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
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvPlaybackViewModel
import org.alsi.android.tvlaba.R
import org.alsi.android.tvlaba.tv.injection.ViewModelFactory
import org.alsi.android.tvlaba.tv.tv.playback.TvPlayerGlue
import javax.inject.Inject

class TvPlaybackAndScheduleFragment : VideoSupportFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: TvPlaybackViewModel

    private lateinit var player: SimpleExoPlayer

    private lateinit var glue: TvPlayerGlue

    private var playback: TvPlayback? = null

    // region Android Life Cycle

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(TvPlaybackViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        player = SimpleExoPlayer.Builder(context).build()

        val playerAdapter = LeanbackPlayerAdapter(
                requireContext(), player, PLAYER_UPDATE_INTERVAL_MILLIS)

        glue = TvPlayerGlue(context, playerAdapter).apply {

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
        viewModel.getLiveData().observe(this,
                Observer<Resource<TvPlayback>> {
                    if (it != null) handlePlaybackRequestEvent(it)
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.dispose()
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

            val dataSourceFactory = DefaultHttpDataSourceFactory(
                    Util.getUserAgent(it, getString(R.string.app_name)))
            val hlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(playback.streamUri.toString()))

            player.prepare(hlsMediaSource, false, true)
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