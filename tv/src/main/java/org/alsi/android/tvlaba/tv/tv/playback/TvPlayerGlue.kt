package org.alsi.android.tvlaba.tv.tv.playback

import android.content.Context
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import org.alsi.android.domain.tv.model.guide.TvPlayback
import kotlin.math.max
import kotlin.math.min

class TvPlayerGlue (context: Context, adapter: LeanbackPlayerAdapter) :
        PlaybackTransportControlGlue<LeanbackPlayerAdapter>(context, adapter) {

    private val actionRewind = PlaybackControlsRow.RewindAction(context)
    private val actionFastForward = PlaybackControlsRow.FastForwardAction(context)
    private val actionClosedCaptions = PlaybackControlsRow.ClosedCaptioningAction(context)

    private fun skipForward(millis: Long = SEEK_STEP_MILLIS) =
            // Ensures we don't advance past the content duration (if set)
            playerAdapter.seekTo(if (playerAdapter.duration > 0) {
                min(playerAdapter.duration, playerAdapter.currentPosition + millis)
            } else {
                playerAdapter.currentPosition + millis
            })

    private fun skipBackward(millis: Long = SEEK_STEP_MILLIS) =
            playerAdapter.seekTo(max(0, playerAdapter.currentPosition - millis))

    override fun onCreatePrimaryActions(adapter: ArrayObjectAdapter) {
        super.onCreatePrimaryActions(adapter)
        adapter.add(actionRewind)
        adapter.add(actionFastForward)
        adapter.add(actionClosedCaptions)
    }

    override fun onActionClicked(action: Action) = when (action) {
        actionRewind -> skipBackward()
        actionFastForward -> skipForward()
        else -> super.onActionClicked(action)
    }

    fun setMetadata(playback: TvPlayback) {
        title = playback.title
        subtitle = playback.description
    }

    companion object {

        /** Default time used when skipping playback in milliseconds */
        private val SEEK_STEP_MILLIS: Long = java.util.concurrent.TimeUnit.MINUTES.toMillis(1)
        private val FAST_SEEK_STEP_MILLIS: Long = java.util.concurrent.TimeUnit.MINUTES.toMillis(5)
    }
}