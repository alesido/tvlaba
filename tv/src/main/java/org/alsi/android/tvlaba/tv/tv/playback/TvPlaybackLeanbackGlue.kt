package org.alsi.android.tvlaba.tv.tv.playback

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramDisposition
import kotlin.math.max
import kotlin.math.min


class TvPlaybackLeanbackGlue (context: Context, adapter: LeanbackPlayerAdapter) :
        PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, adapter) {

    private val actionRewind = PlaybackControlsRow.RewindAction(context)
    private val actionFastForward = PlaybackControlsRow.FastForwardAction(context)
    private val actionClosedCaptions = PlaybackControlsRow.ClosedCaptioningAction(context)

    private var playback: TvPlayback? = null
    private var initialDisposition: TvProgramDisposition? = null

    private var overriddenDuration: Long? = null
    private val wrappedDuration get() = overriddenDuration?: playerAdapter.duration

    private var maintainLivePosition: Boolean = false
    private var pausePosition: Long = -1L


    fun bindPlaybackItem(playback: TvPlayback): Boolean {
        when(playback.disposition) {
            TvProgramDisposition.LIVE -> configureLivePlayback()
            TvProgramDisposition.RECORD -> configureArchivePlayback()
            else -> return false
        }
        this.playback = playback
        title = playback.title
        subtitle = playback.description
        return true
    }

    private fun configureLivePlayback() {
        playback?: return
        if (null == playback!!.time) {
            isSeekEnabled = false
            maintainLivePosition = true
        }
        else {
            isSeekEnabled = true
            with(playback!!.time!!) {
                overrideDuration(endUnixTimeMillis - startUnixTimeMillis)
                maintainLivePosition = true
            }
        }
        initialDisposition = TvProgramDisposition.LIVE
    }

    private fun configureArchivePlayback() {
        playback?.time?: return
        isSeekEnabled = true
        with(playback!!.time!!) {
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

    private fun beep() {
        ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                .startTone(ToneGenerator.TONE_CDMA_PIP, 150)
    }

    override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter) {
        super.onCreatePrimaryActions(adapter)
        adapter.add(actionRewind)
        adapter.add(actionFastForward)
        //adapter.add(actionClosedCaptions)
    }

    override fun onActionClicked(action: Action) {
        if (action.label1 == "Play") {
            pausePosition = if (playerAdapter.isPlaying) currentPosition else -1L
            return
        }
        when (action) {
            actionRewind -> skipBackward()
            actionFastForward -> skipForward()
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
        private val SEEK_STEP_MILLIS: Long = java.util.concurrent.TimeUnit.MINUTES.toMillis(1)
        private val FAST_SEEK_STEP_MILLIS: Long = java.util.concurrent.TimeUnit.MINUTES.toMillis(5)
    }
}