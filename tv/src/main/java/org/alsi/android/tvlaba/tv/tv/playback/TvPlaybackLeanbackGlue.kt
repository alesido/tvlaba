package org.alsi.android.tvlaba.tv.tv.playback

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.*
import androidx.preference.PreferenceManager
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlFunction
import org.alsi.android.domain.streaming.model.options.rc.RemoteControlFunction.*
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramDisposition
import org.alsi.android.framework.formatMillis
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.alsi.android.presentationtv.model.TvPlaybackViewModel
import org.alsi.android.tvlaba.R
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min


class TvPlaybackLeanbackGlue(

        context: Context,
        adapter: LeanbackPlayerAdapter,
        val model: TvPlaybackViewModel,
        val isControlsVisible: () -> Boolean

) : PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, adapter) {

    /** Set of actions bound to player controls
     */
    private val actions = TvPlaybackActions(context, model)

    /** Currently bound playback item
     */
    var playback: TvPlayback? = null

    private lateinit var _primaryActionsAdapter: ArrayObjectAdapter
    private lateinit var _secondaryActionsAdapter: ArrayObjectAdapter

    private val controlButtonPresenterSelector: ControlButtonPresenterSelector
    = TvControlButtonPresenterSelector()

    private var showPlaybackProgress = true

    private var initialDisposition: TvProgramDisposition? = null

    private var overriddenDuration: Long? = null
    private val wrappedDuration get() = overriddenDuration?: playerAdapter.duration

    private var maintainLivePosition: Boolean = false
    private var pausePosition: Long = -1L

    /**
     *  ... to avoid seek when a long time processing takes place, e.g. switching from
     *  live to archive, or to a next previous program
     */
    private var isInSeekTransition = false

    private lateinit var onVideoOptionsControlClicked: () -> Unit

    private lateinit var playbackRowViewHolder: PlaybackTransportRowPresenter.ViewHolder

    private val playbackRowPresenter: PlaybackTransportRowPresenter = object : PlaybackTransportRowPresenter() {

        private var playerTimeFontSize =  preferredFontSize()

//        init {
//            val typedValue = TypedValue()
//            if (context.theme.resolveAttribute(R.attr.text_large, typedValue, true)) {
//                playerTimeFontSize = typedValue.data.toFloat() // returns ~5940 for medium, ~6190 for large font ?
//            }
//        }

        override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
            val vh = super.createRowViewHolder(parent)
            listOf(R.id.current_time, R.id.separate_time, R.id.total_time).forEach {
                (vh.view.findViewById<View>(it) as TextView).textSize = playerTimeFontSize
            }
            return vh
        }

        override fun onBindRowViewHolder(vh: RowPresenter.ViewHolder, item: Any) {
            super.onBindRowViewHolder(vh, item)
            playbackRowViewHolder = vh as PlaybackTransportRowPresenter.ViewHolder
            vh.onKeyListener = this@TvPlaybackLeanbackGlue
            setPlaybackProgressVisibility(vh,
                if (showPlaybackProgress) View.VISIBLE else View.INVISIBLE)
        }

        override fun onUnbindRowViewHolder(vh: RowPresenter.ViewHolder) {
            super.onUnbindRowViewHolder(vh)
            vh.onKeyListener = null
        }

        fun setPlaybackProgressVisibility(vh: RowPresenter.ViewHolder, visibility: Int) {
            listOf(R.id.playback_progress, R.id.current_time, R.id.separate_time, R.id.total_time)
                .forEach { vh.view.findViewById<View>(it).visibility = visibility }
        }

        private fun preferredFontSize(): Float =
            when (PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_key_font_size), "medium")) {
                "small" -> 24f; "medium" -> 32f; "large" -> 48f; else -> 32f
            }
    }

    override fun onCreateRowPresenter(): PlaybackRowPresenter {
        playbackRowPresenter.setDescriptionPresenter(TvProgramPlaybackDetailsPresenter(context))
        return playbackRowPresenter
    }

    // endregion
    // region Playback Setup

    fun bindPlaybackItem(playback: TvPlayback): Boolean {
        when(playback.stream?.kind) {
            VideoStreamKind.LIVE -> configureLivePlayback(playback)
            VideoStreamKind.RECORD -> configureArchivePlayback(playback)
            else -> return false
        }
        this.playback = playback
        isInSeekTransition = false // in case this initiated. e.g. by live to record switch
        return true
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

    private fun seekIncrement() = (playbackRowPresenter.defaultSeekIncrement
            * playback!!.time!!.durationMillis).toLong()

    private fun seekIncrement(p: TvPlayback) = (playbackRowPresenter.defaultSeekIncrement
            * p.time!!.durationMillis).toLong()

    private fun isLive() = playback?.disposition == TvProgramDisposition.LIVE
            && playback?.isLiveRecord != true

    private fun isLiveRecord() = playback?.disposition == TvProgramDisposition.LIVE
            && playback?.isLiveRecord == true

    private fun isRecord() = playback?.disposition == TvProgramDisposition.RECORD

    private fun configureLivePlayback(playback: TvPlayback) {
        if (null == playback.time) {
            // there is no program for this channel
            isSeekEnabled = false
            overrideDuration(TimeUnit.DAYS.toMillis(1))
            maintainLivePosition = true
            setupRowsForNoScheduleLive()
            showPlaybackProgress = false
        }
        else {
            isSeekEnabled = true // made enabled when the "live record" has got ready
            with(playback.time!!) {
                overrideDuration(endUnixTimeMillis - startUnixTimeMillis)
                maintainLivePosition = true
                setupRows()
            }
            showPlaybackProgress = true
        }
        initialDisposition = TvProgramDisposition.LIVE
        playback.isLiveRecord = playback.record?.let { false }
        setSeekController(SeekController())
    }

    private fun configureLiveRecordPlayback(playback: TvPlayback) {
        playback.time?: return
        isSeekEnabled = true
        with(playback.time!!) {
            overrideDuration(endUnixTimeMillis - startUnixTimeMillis)
            maintainLivePosition = false
            setupRows()
        }
        initialDisposition = TvProgramDisposition.RECORD
        setSeekController(SeekController())
        showPlaybackProgress = true
    }

    private fun configureArchivePlayback(playback: TvPlayback) {
        playback.time?: return
        isSeekEnabled = true
        with(playback.time!!) {
            overrideDuration(endUnixTimeMillis - startUnixTimeMillis)
            maintainLivePosition = false
            setupRows()
        }
        initialDisposition = TvProgramDisposition.RECORD
        setSeekController(SeekController())
        showPlaybackProgress = true
    }

    /**
     * @return True, if playback have to be paused at the start
     */
    fun handlePlaybackStart(resource: Resource<TvPlayback>): Boolean {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                resource.data?.let {
                    // in case it started while rewinding from the next video:
                    // offset position back to not reach the end immediately
                    if (it.disposition == TvProgramDisposition.RECORD && it.time?.durationMillis != null) {
                        val seekIncrement = seekIncrement(it)
                        if (it.time!!.durationMillis - it.position <= seekIncrement)
                            it.position -= seekIncrement
                    }
                    // reset position after crossing a boundary
                    if (isInSeekTransition && it.time?.durationMillis != null)
                        playbackRowViewHolder.forceProgressPosition(it.position,
                            it.time!!.durationMillis)
                    // request pause when prepared, because video boundary crossed while seeking
                    val z = isInSeekTransition
                    isInSeekTransition = false
                    return z
                }
            }
            else -> { isInSeekTransition = false }
        }
        return false // do not pause playback when prepared
    }

    /** Switch from record of live to live itself.
     *  NOTE It's supposed that stream URL of the live record will be valid during the live playback.
     */
    private fun switchToLiveRecordPlayback() {
        playback!!.position = currentPosition - seekIncrement()
        isInSeekTransition = true
        model.getLiveRecordStream(playback!!)
    }

    fun handleLiveRecordStreamData(
        resource: Resource<VideoStream>,
        playLiveRecordStream: ((VideoStream, Long) -> Unit)? = null
    ) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                playback?.let {
                    playLiveRecordStream?.let { it(resource.data!!, playback!!.position) }
                    configureLiveRecordPlayback(playback!!)
                    isInSeekTransition = false
                }
            }
            ResourceState.ERROR -> switchToLivePlayback()
            else -> { isInSeekTransition = false }
        }
    }

    /** Switch from live to its record.
     */
    private fun switchToLivePlayback() {
        isInSeekTransition = true
        model.regetLiveStream(playback!!)
    }

    fun handleLiveStreamDataOnRestart(
        resource: Resource<VideoStream>,
        playLiveStream: ((VideoStream) -> Unit)? = null
    ) {
        when (resource.status) {
            ResourceState.SUCCESS -> {
                playback?.let {
                    playLiveStream?.let { it(resource.data!!) }
                    configureLivePlayback(playback!!)
                    isInSeekTransition = false

                }
            }
            else -> { isInSeekTransition = false }
        }
    }

    // endregion
    // region Playback events

    fun onEarlyCompletion() {
//        playback?.time?: return
//        if (playback?.disposition == TvProgramDisposition.LIVE &&
//            playback?.stream?.kind?:VideoStreamKind.UNKNOWN == VideoStreamKind.RECORD) {
//            // live record have reached current time boundary
//            Timber.d("@onEarlyCompletion switchToLivePlayback")
//            switchToLivePlayback()
//        }
    }

    // endregion
    // region Seek Controller

    /**
     * This is to control seeks as user interacts with the playback's progress bar
     */
    inner class SeekController: PlaybackSeekUi.Controller {

        override fun consumeSeekStart(): Boolean {
            if (isLive() && playback?.record?.isEmpty() != true) {
                switchToLiveRecordPlayback()
            }
            return false
        }

        override fun consumeSeekPositionChange(targetPosition: Long): Boolean {
            if (isInSeekTransition)
                return true

            val seekIncrement = seekIncrement()

            // handle forward seek beyond the live edge
            if (isLiveRecord()
                && playback?.isSeekBeyondLiveEdge(targetPosition, seekIncrement) == true) {
                switchToLivePlayback()
                return false
            }

            // handle seeking beyond record start or end

            val isForwardSeek = currentPosition < targetPosition
                    || (currentPosition == targetPosition
                        && duration - targetPosition < seekIncrement)

            if (duration - currentPosition < seekIncrement && isForwardSeek && !isLiveRecord()) {
                // seek is going beyond the end
                next()
                return false
            }

            if (currentPosition < seekIncrement && !isForwardSeek) {
                // seek is going beyond the beginning
                previous()
                return false
            }

            return false
        }

        override fun consumeSeekFinished(cancelled: Boolean, targetPosition: Long): Boolean {
            return false
        }
    }

    // endregion
    // region Actions Binding

    /** This is to handle seek when user presses the action button in the player's control panel
     */
    private fun skipForward(millis: Long = SEEK_STEP_MILLIS) {
        playback?.stream?: return

        val nextPosition = currentPosition + millis

        if (playback?.disposition == TvProgramDisposition.LIVE) {

            // check if live record's end boundary reached
            if (playback?.stream?.kind == VideoStreamKind.RECORD) {

                if (playback?.isSeekBeyondLiveEdge(nextPosition,
                        THRESHOLD_LIVE_RECORD_COMPLETE_MILLIS) == true) {
                    Timber.d("@skipForward: switch to LIVE")
                    playback?.let { switchToLivePlayback() }
                }
                else {
                    playback?.position = nextPosition
                    playerAdapter.seekTo(nextPosition)
                    Timber.d("@skipForward: to %s/%s",
                        formatMillis(nextPosition), formatMillis(playerAdapter.duration))
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

    /** This is to handle seek when user presses the action button in the player's control panel
     */
    private fun skipBackward(millis: Long = SEEK_STEP_MILLIS) {
        if (playback?.disposition == TvProgramDisposition.LIVE) {
            if (playback?.stream?.kind == VideoStreamKind.LIVE) {
                playback?.let { switchToLiveRecordPlayback() }
            }
            beep(); return
        }

        playerAdapter.seekTo(max(0, currentPosition - millis))
    }

    override fun previous() {
        isInSeekTransition = true
        model.onPreviousProgramAction()
    }

    override fun next() {
        isInSeekTransition = true
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
        _primaryActionsAdapter = primaryActionsAdapter
        _primaryActionsAdapter.presenterSelector = controlButtonPresenterSelector
    }

    override fun onCreateSecondaryActions(secondaryActionsAdapter: ArrayObjectAdapter) {
        super.onCreateSecondaryActions(secondaryActionsAdapter)
        actions.setupSecondaryRow(secondaryActionsAdapter)
        _secondaryActionsAdapter = secondaryActionsAdapter
        _secondaryActionsAdapter.presenterSelector = controlButtonPresenterSelector
    }

    private var isLastRowsSetupForNoScheduleLive: Boolean? = null

    private fun setupRows() {
        if (isLastRowsSetupForNoScheduleLive == false)
            return
        isLastRowsSetupForNoScheduleLive = false
        // clear out custom primary actions
        for (i in (_primaryActionsAdapter.size() - 1) downTo 1)
            _primaryActionsAdapter.remove(_primaryActionsAdapter[i])
        // NOTE Do not try to clear and recreate actions: it interferes with change notification scheme
        actions.setupPrimaryRow(_primaryActionsAdapter)
        _secondaryActionsAdapter.clear()
        super.onCreateSecondaryActions(_secondaryActionsAdapter)
        actions.setupSecondaryRow(_secondaryActionsAdapter)
    }

    private fun setupRowsForNoScheduleLive() {
        if (isLastRowsSetupForNoScheduleLive == true)
            return
        isLastRowsSetupForNoScheduleLive = true
        // clear out custom primary actions
        for (i in (_primaryActionsAdapter.size() - 1) downTo 1)
            _primaryActionsAdapter.remove(_primaryActionsAdapter[i])
        // NOTE Do not try to clear and recreate actions: it interferes with change notification scheme
        actions.setupPrimaryRowForNoScheduleLive(_primaryActionsAdapter)
        _secondaryActionsAdapter.clear()
        super.onCreateSecondaryActions(_secondaryActionsAdapter)
        actions.setupSecondaryRowForNoScheduleLive(_secondaryActionsAdapter)
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
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                // block seek forward for live stream, while allowing to open controls
                // with DPAD Right key press
                // FIXME Improve criteria to match seek forward on a live playback
                return isControlsVisible() && isLive() && v?.findFocus() is SeekBar
            }
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN,
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
    // region Control Panel Updating

    @SuppressLint("MissingSuperCall")
    override fun onUpdateProgress() {
        if (controlsRow != null && !isInSeekTransition) {
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
        private val SEEK_STEP_MILLIS = TimeUnit.MINUTES.toMillis(1)
        private val FAST_SEEK_STEP_MILLIS = TimeUnit.MINUTES.toMillis(5)

        private val THRESHOLD_LIVE_RECORD_COMPLETE_MILLIS = TimeUnit.SECONDS.toMillis(3)
        private const val THRESHOLD_PLAYBACK_EDGE_REACHED = 2
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
// decided to remove rewind and forward buttons because they duplicate seeking with
// the progress bar and complicates player controls using
//        adapter.add(rewind)
//        adapter.add(forward)
        adapter.add(prevProgram)
        adapter.add(nextProgram)
    }

    fun setupSecondaryRow(adapter: ArrayObjectAdapter) {
        adapter.add(videoOptions) // right below play/pause
// decided to remove fast rewind and fast forward buttons, increase/decrease seeking speed
// with DPAD_UP/DOWN in seeking mode
//        adapter.add(fasterRewind)
//        adapter.add(fasterForward)
        adapter.add(prevChannel)
        adapter.add(nextChannel)
    }

    fun setupPrimaryRowForNoScheduleLive(adapter: ArrayObjectAdapter) {
        // play/pause added with super call
        adapter.add(videoOptions)
        adapter.add(prevChannel)
        adapter.add(nextChannel)
    }

    fun setupSecondaryRowForNoScheduleLive(
        @Suppress("UNUSED_PARAMETER") adapter: ArrayObjectAdapter) {
        // there are not enough actions to use secondary row
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
// This throws "java.lang.RuntimeException: Init failed" at org.alsi.android.tvlaba.tv.tv.playback.TvPlaybackLeanbackGlueKt.beep(TvPlaybackLeanbackGlue.kt:379)
//    ToneGenerator(AudioManager.STREAM_MUSIC, 100)
//            .startTone(ToneGenerator.TONE_CDMA_PIP, 150)
}
