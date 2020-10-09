package org.alsi.android.tvlaba.tv.tv.playback

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramDisposition
import org.alsi.android.presentationtv.model.TvPlaybackViewModel
import org.alsi.android.tvlaba.R
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min


class TvPlaybackLeanbackGlue(

        context: Context,
        adapter: LeanbackPlayerAdapter,
        val model: TvPlaybackViewModel

) : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, adapter) {

    /** Set of actions bound to player controls
     */
    private val actions = TvPlaybackActions(context)

    /** Currently bound playback item
     */
    var playback: TvPlayback? = null

    private var initialDisposition: TvProgramDisposition? = null

    private var overriddenDuration: Long? = null
    private val wrappedDuration get() = overriddenDuration?: playerAdapter.duration

    private var maintainLivePosition: Boolean = false
    private var pausePosition: Long = -1L

    override fun onCreateRowPresenter(): PlaybackRowPresenter {

        val rowPresenter: PlaybackTransportRowPresenter = object : PlaybackTransportRowPresenter() {
            override fun onBindRowViewHolder(vh: RowPresenter.ViewHolder, item: Any) {
                super.onBindRowViewHolder(vh, item)
                vh.onKeyListener = this@TvPlaybackLeanbackGlue
        }

        override fun onUnbindRowViewHolder(vh: RowPresenter.ViewHolder) {
                super.onUnbindRowViewHolder(vh)
                vh.onKeyListener = null
            }
        }

        rowPresenter.setDescriptionPresenter(TvProgramPlaybackDetailsPresenter(context))
        return rowPresenter
    }

    fun bindPlaybackItem(playback: TvPlayback): Boolean {
        when(playback.disposition) {
            TvProgramDisposition.LIVE -> configureLivePlayback(playback)
            TvProgramDisposition.RECORD -> configureArchivePlayback(playback)
            else -> return false
        }
        this.playback = playback
        this.title
        return true
    }

    private fun configureLivePlayback(playback: TvPlayback) {
        if (null == playback.time) {
            isSeekEnabled = false
            overrideDuration(TimeUnit.DAYS.toMillis(1))
            maintainLivePosition = true
        }
        else {
            isSeekEnabled = false // enable when "live record" is ready
            with(playback.time!!) {
                overrideDuration(endUnixTimeMillis - startUnixTimeMillis)
                maintainLivePosition = true
            }
        }
        initialDisposition = TvProgramDisposition.LIVE
    }

    private fun configureArchivePlayback(playback: TvPlayback) {
        playback.time?: return
        isSeekEnabled = true
        with(playback.time!!) {
            overrideDuration(endUnixTimeMillis - startUnixTimeMillis)
            maintainLivePosition = false
        }
        initialDisposition = TvProgramDisposition.RECORD
    }

    override fun getCurrentPosition() =
        if (maintainLivePosition && playback?.time != null) {
            if (playerAdapter.isPlaying)
                System.currentTimeMillis() - playback!!.time!!.startUnixTimeMillis
            else
                pausePosition
        }
        else {
            playerAdapter.currentPosition
        }

    private fun overrideDuration(overriddenDuration: Long) {
        this.overriddenDuration = overriddenDuration
    }

    private fun skipForward(millis: Long = SEEK_STEP_MILLIS) {
        if (initialDisposition == TvProgramDisposition.LIVE) { beep(); return }
        // Ensures we don't advance past the content duration (if set)
        playerAdapter.seekTo(if (wrappedDuration > 0) {
            min(wrappedDuration, currentPosition + millis)
        } else {
            currentPosition + millis
        })
    }

    private fun skipBackward(millis: Long = SEEK_STEP_MILLIS) {
        if (initialDisposition == TvProgramDisposition.LIVE) { beep(); return }
        playerAdapter.seekTo(max(0, currentPosition - millis))
    }

    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreatePrimaryActions(primaryActionsAdapter)
        actions.setupPrimaryRow(primaryActionsAdapter)
    }

    override fun onCreateSecondaryActions(secondaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreateSecondaryActions(secondaryActionsAdapter)
        actions.setupSecondaryRow(secondaryActionsAdapter)
    }

    override fun onActionClicked(action: Action) {
          if (actions.isPlayPauseAction(action)) {
            pausePosition = if (playerAdapter.isPlaying) currentPosition else -1L
            super.onActionClicked(action)
            return
        }
        when (action) {
            actions.slowRewind -> skipBackward()
            actions.slowForward -> skipForward()
            actions.fastRewind -> skipBackward(FAST_SEEK_STEP_MILLIS)
            actions.fastForward -> skipForward(FAST_SEEK_STEP_MILLIS)
            actions.prevChannel -> model.onPreviousChannelAction()
            actions.nextChannel -> model.onNextChannelAction()
            else -> super.onActionClicked(action)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onUpdateProgress() {
        if (controlsRow != null) {
            controlsRow.currentPosition = if (playerAdapter.isPrepared) currentPosition else -1
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onUpdateBufferedProgress() {
        if (controlsRow != null) {
            controlsRow.bufferedPosition = playerAdapter.bufferedPosition
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onUpdateDuration() {
        if (controlsRow != null) {
            controlsRow.duration = if (playerAdapter.isPrepared) wrappedDuration else -1
        }
    }

    companion object {

        /** Default time used when skipping playback in milliseconds */
        private val SEEK_STEP_MILLIS: Long = TimeUnit.MINUTES.toMillis(1)
        private val FAST_SEEK_STEP_MILLIS: Long = TimeUnit.MINUTES.toMillis(5)
    }
}

class TvPlaybackActions(val context: Context) {

    val slowRewind = createAction(R.drawable.ic_rewind_slow, R.string.label_rewind_slow)
    val slowForward = createAction(R.drawable.ic_forward_slow, R.string.label_forward_slow)

    val fastRewind = PlaybackControlsRow.RewindAction(context)
    val fastForward = PlaybackControlsRow.FastForwardAction(context)

    val prevProgram = PlaybackControlsRow.SkipPreviousAction(context)
    val nextProgram = PlaybackControlsRow.SkipNextAction(context)

    val prevChannel = createAction(R.drawable.ic_channel_minus, R.string.label_previous_channel)
    val nextChannel = createAction(R.drawable.ic_channel_plus, R.string.label_next_channel)

    val language = createAction(R.drawable.ic_language, R.string.label_language)
    val aspectRatio = createAction(R.drawable.ic_aspect_ratio, R.string.label_aspect_ratio)


    fun setupPrimaryRow(adapter: ArrayObjectAdapter) {
        // play/pause assumed is here by default
        adapter.add(slowRewind)
        adapter.add(slowForward)
        adapter.add(prevProgram)
        adapter.add(nextProgram)
    }

    fun setupSecondaryRow(adapter: ArrayObjectAdapter) {
        adapter.add(language) // right below play/pause
        adapter.add(fastRewind)
        adapter.add(fastForward)
        adapter.add(prevChannel)
        adapter.add(nextChannel)
        adapter.add(aspectRatio)
    }

    fun isPlayPauseAction(action: Action) =
            action.id == androidx.leanback.R.id.lb_control_play_pause.toLong()

    private fun createAction(iconRes: Int, labelRes: Int): Action {
        val action = Action(iconRes.toLong(), context.resources.getString(labelRes))
        action.icon = ResourcesCompat.getDrawable(context.resources, iconRes, null)
        return action
    }
}

fun beep() {
    ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            .startTone(ToneGenerator.TONE_CDMA_PIP, 150)
}