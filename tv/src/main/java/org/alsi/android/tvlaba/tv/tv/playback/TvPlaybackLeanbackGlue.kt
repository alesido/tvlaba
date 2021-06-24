package org.alsi.android.tvlaba.tv.tv.playback

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlFunction
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlFunction.*
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramDisposition
import org.alsi.android.framework.formatMillis
import org.alsi.android.presentationtv.model.TvPlaybackViewModel
import org.alsi.android.tvlaba.R
import timber.log.Timber
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
    private val actions = TvPlaybackActions(context, model)

    /** Currently bound playback item
     */
    var playback: TvPlayback? = null

    private var initialDisposition: TvProgramDisposition? = null

    private var overriddenDuration: Long? = null
    private val wrappedDuration get() = overriddenDuration?: playerAdapter.duration

    private var maintainLivePosition: Boolean = false
    private var pausePosition: Long = -1L

    private lateinit var onVideoOptionsControlClicked: () -> Unit

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

    // region Playback Setup

    fun bindPlaybackItem(playback: TvPlayback): Boolean {
        when(playback.stream?.kind) {
            VideoStreamKind.LIVE -> configureLivePlayback(playback)
            VideoStreamKind.RECORD -> configureArchivePlayback(playback)
            else -> return false
        }
        this.playback = playback
        return true
    }

    private fun configureLivePlayback(playback: TvPlayback) {
        if (null == playback.time) {
            // no program channel
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
        setSeekController(SeekController())
    }

    private fun configureArchivePlayback(playback: TvPlayback) {
        playback.time?: return
        isSeekEnabled = true
        with(playback.time!!) {
            overrideDuration(endUnixTimeMillis - startUnixTimeMillis)
            maintainLivePosition = false
        }
        initialDisposition = TvProgramDisposition.RECORD
        setSeekController(SeekController())
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


    fun onEarlyCompletion() {
        playback?.time?: return
        if (playback?.disposition == TvProgramDisposition.LIVE &&
                playback?.stream?.kind?:VideoStreamKind.UNKNOWN == VideoStreamKind.RECORD) {
            // live record have reached current time boundary
            Timber.d("@onEarlyCompletion switchToLivePlayback")
            model.switchToLivePlayback(playback!!)
        }
    }


    // endregion
    // region Actions Binding

    private fun skipForward(millis: Long = SEEK_STEP_MILLIS) {
        playback?.stream?: return

        val nextPosition = currentPosition + millis

        if (playback?.disposition == TvProgramDisposition.LIVE) {

            // check if live record's end boundary reached
            if (playback?.stream?.kind == VideoStreamKind.RECORD) {

                val endPosition = playerAdapter.duration
                if (nextPosition > endPosition - 1_000L) {
                    Timber.d("@skipForward: switch to LIVE")
                    playback?.let { model.switchToLivePlayback(it) }
                }
                else {
                    playback?.position = nextPosition
                    playerAdapter.seekTo(nextPosition)
                    Timber.d("@skipForward: to %s/%s", formatMillis(nextPosition), formatMillis(endPosition))
                }
                return
            }

            beep(); return
        }

        // move to a next video or seek to a next position
        if (nextPosition > min(wrappedDuration, playerAdapter.duration) - 1_000L) {
            next()
        }
        else {
            playerAdapter.seekTo(nextPosition)
        }
    }

    private fun skipBackward(millis: Long = SEEK_STEP_MILLIS) {
        if (playback?.disposition == TvProgramDisposition.LIVE) {
            if (playback?.stream?.kind == VideoStreamKind.LIVE) {
                playback?.let { model.switchToArchivePlayback(it) }
            }
            beep(); return
        }

        playerAdapter.seekTo(max(0, currentPosition - millis))
    }

    override fun previous() {
        model.onPreviousProgramAction()
    }

    override fun next() {
        model.onNextProgramAction()
    }


    fun setPreferencesCallback(f: () -> Unit) {
        onVideoOptionsControlClicked = f
    }

    private fun videoOptions() {
        onVideoOptionsControlClicked()
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
            actions.rewind -> skipBackward()
            actions.forward -> skipForward()
            actions.fasterRewind -> skipBackward(FAST_SEEK_STEP_MILLIS)
            actions.fasterForward -> skipForward(FAST_SEEK_STEP_MILLIS)
            actions.prevProgram -> previous()
            actions.nextProgram -> next()
            actions.prevChannel -> model.onPreviousChannelAction()
            actions.nextChannel -> model.onNextChannelAction()
            actions.videoOptions -> onVideoOptionsControlClicked()
            else -> super.onActionClicked(action)
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
        // prevent redefinition of DPAD functions providing basic leanback navigation
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_ESCAPE
            -> return false
        }
        // consume RC function if defined
        val action = actions.getActionForKeyCode(keyCode) ?: return false
        if (event.action == KeyEvent.ACTION_DOWN) {
            onActionClicked(action)
        }
        return true
    }

    // endregion
    // region Seek Controller

    inner class SeekController: PlaybackSeekUi.Controller {

        override fun consumeSeekStart(): Boolean {
            return false
        }

        override fun consumeSeekPositionChange(pos: Long): Boolean {
            return false
        }

        override fun consumeSeekFinished(cancelled: Boolean, pos: Long): Boolean {
            return false
        }
    }

    // endregion
    // region Control Panel Updating

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

    // endregion
    // region Companion

    companion object {

        /** Default time used when skipping playback in milliseconds */
        private val SEEK_STEP_MILLIS: Long = TimeUnit.MINUTES.toMillis(1)
        private val FAST_SEEK_STEP_MILLIS: Long = TimeUnit.MINUTES.toMillis(5)
    }

    // endregion
}

// region Actions Definition

class TvPlaybackActions(val context: Context, val model: TvPlaybackViewModel) {

    private val actionByRemoteControlFunction: MutableMap<RemoteControlFunction, Action> = mutableMapOf()
    private val remoteControlFunctionByInputKey: MutableMap<Int, RemoteControlFunction> = mutableMapOf()

    val rewind = createAction(REWIND_LEFT, R.drawable.ic_rewind_slow, R.string.label_rewind_slow)
    val forward = createAction(REWIND_RIGHT, R.drawable.ic_forward_slow, R.string.label_forward_slow)

    val fasterRewind = createAction(REWIND_LEFT_FASTER,
            R.drawable.ic_rewind_faster, R.string.label_rewind_faster)
    val fasterForward = createAction(REWIND_RIGHT_FASTER,
            R.drawable.ic_forward_faster, R.string.label_forward_faster)

    val prevProgram = createAction(PREVIOUS_PROGRAM,
            R.drawable.ic_previous_program, R.string.label_previous_program)
    val nextProgram = createAction(NEXT_PROGRAM,
            R.drawable.ic_next_program, R.string.label_next_program)

    val prevChannel = createAction(R.drawable.ic_channel_minus, R.string.label_previous_channel)
    val nextChannel = createAction(R.drawable.ic_channel_plus, R.string.label_next_channel)

    val videoOptions = createAction(R.drawable.ic_video_settings, R.string.label_video_options)

    init {
        model.getSettings { settings ->
            settings.rc?.remoteControlKeyCodeMap?.forEach {
                actionByRemoteControlFunction[it.value]?.addKeyCode(it.key)
                remoteControlFunctionByInputKey[it.key] = it.value
            }
        }
    }

    fun setupPrimaryRow(adapter: ArrayObjectAdapter) {
        // play/pause assumed is here by default
        adapter.add(rewind)
        adapter.add(forward)
        adapter.add(prevProgram)
        adapter.add(nextProgram)
    }

    fun setupSecondaryRow(adapter: ArrayObjectAdapter) {
        adapter.add(videoOptions) // right below play/pause
        adapter.add(fasterRewind)
        adapter.add(fasterForward)
        adapter.add(prevChannel)
        adapter.add(nextChannel)
    }

    fun isPlayPauseAction(action: Action) =
            action.id == androidx.leanback.R.id.lb_control_play_pause.toLong()

    fun getActionForKeyCode(keyCode: Int): Action? {
        val rcFunction = remoteControlFunctionByInputKey[keyCode]?: return null
        return actionByRemoteControlFunction[rcFunction]
    }

    private fun createAction(iconRes: Int, labelRes: Int): Action {
        val action = Action(iconRes.toLong(), context.resources.getString(labelRes))
        action.icon = ResourcesCompat.getDrawable(context.resources, iconRes, null)
        return action
    }

    private fun createAction(rcFunction: RemoteControlFunction, iconRes: Int, labelRes: Int): Action {
        val action = createAction(iconRes, labelRes)
        actionByRemoteControlFunction[rcFunction] = action
        return action
    }
}

// endregion

fun beep() {
// This drops "java.lang.RuntimeException: Init failed" at org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackLeanbackGlueKt.beep(TvPlaybackLeanbackGlue.kt:379)
//    ToneGenerator(AudioManager.STREAM_MUSIC, 100)
//            .startTone(ToneGenerator.TONE_CDMA_PIP, 150)
}