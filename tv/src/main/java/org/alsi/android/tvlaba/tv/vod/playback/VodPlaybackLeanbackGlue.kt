package org.alsi.android.tvlaba.tv.vod.playback

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.*
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlFunction
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.presentationvod.model.VodPlaybackViewModel
import org.alsi.android.tvlaba.R
import java.util.concurrent.TimeUnit
import kotlin.math.max

class VodPlaybackLeanbackGlue(

    context: Context,
    adapter: LeanbackPlayerAdapter,
    val model: VodPlaybackViewModel

) : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, adapter) {

    /** Set of actions bound to player controls
     */
    private val actions = VodPlaybackActions(context, model)

    /** Currently bound playback item
     */
    var playback: VodPlayback? = null

    private lateinit var onVideoOptionsControlClicked: () -> Unit

    override fun onCreateRowPresenter(): PlaybackRowPresenter {

        val rowPresenter: PlaybackTransportRowPresenter = object : PlaybackTransportRowPresenter() {
            override fun onBindRowViewHolder(vh: RowPresenter.ViewHolder, item: Any) {
                super.onBindRowViewHolder(vh, item)
                vh.onKeyListener = this@VodPlaybackLeanbackGlue
            }

            override fun onUnbindRowViewHolder(vh: RowPresenter.ViewHolder) {
                super.onUnbindRowViewHolder(vh)
                vh.onKeyListener = null
            }
        }

        rowPresenter.setDescriptionPresenter(VodItemPlaybackDetailsPresenter(context))
        return rowPresenter
    }

    // region Playback Setup

    fun bindPlaybackItem(playback: VodPlayback): Boolean {
        this.playback = playback
        isSeekEnabled = true
        setSeekController(SeekController())
        return true
    }

    // endregion
    // region Actions Binding


    override fun onCreatePrimaryActions(primaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreatePrimaryActions(primaryActionsAdapter)
        actions.setupPrimaryRow(primaryActionsAdapter)
    }

    override fun onCreateSecondaryActions(secondaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreateSecondaryActions(secondaryActionsAdapter)
        actions.setupSecondaryRow(secondaryActionsAdapter)
    }

    override fun onActionClicked(action: Action) {
        when (action) {
            actions.rewind -> playerAdapter
                .seekTo(max(0, currentPosition - SEEK_STEP_MILLIS))
            actions.forward -> playerAdapter
                .seekTo(currentPosition + SEEK_STEP_MILLIS)
            actions.fasterRewind -> playerAdapter
                .seekTo(max(0, currentPosition - FAST_SEEK_STEP_MILLIS))
            actions.fasterForward -> playerAdapter
                .seekTo(currentPosition + FAST_SEEK_STEP_MILLIS)
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

    fun setPreferencesCallback(f: () -> Unit) {
        onVideoOptionsControlClicked = f
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

    //endregion
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

    //endregion
    companion object {

        /** Default time used when skipping playback in milliseconds */
        private val SEEK_STEP_MILLIS: Long = TimeUnit.MINUTES.toMillis(1)
        private val FAST_SEEK_STEP_MILLIS: Long = TimeUnit.MINUTES.toMillis(5)
    }
}

//region Actions

class VodPlaybackActions(val context: Context, val model: VodPlaybackViewModel) {

    private val actionByRemoteControlFunction: MutableMap<RemoteControlFunction, Action> = mutableMapOf()
    private val remoteControlFunctionByInputKey: MutableMap<Int, RemoteControlFunction> = mutableMapOf()

    val rewind = createAction(RemoteControlFunction.REWIND_LEFT,
        R.drawable.ic_rewind_slow, R.string.label_rewind_slow)
    val forward = createAction(RemoteControlFunction.REWIND_RIGHT,
        R.drawable.ic_forward_slow, R.string.label_forward_slow)

    val fasterRewind = createAction(
        RemoteControlFunction.REWIND_LEFT_FASTER,
        R.drawable.ic_rewind_faster, R.string.label_rewind_faster)
    val fasterForward = createAction(
        RemoteControlFunction.REWIND_RIGHT_FASTER,
        R.drawable.ic_forward_faster, R.string.label_forward_faster)

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
    }

    fun setupSecondaryRow(adapter: ArrayObjectAdapter) {
        adapter.add(videoOptions) // right below play/pause
        adapter.add(fasterRewind)
        adapter.add(fasterForward)
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

//endregion