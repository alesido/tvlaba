package org.alsi.android.tvlaba.framework

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.RendererCapabilities
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider
import org.alsi.android.presentationtv.model.LanguageTrackSelection

class ExoplayerTrackLanguageSelection (context: Context) : LanguageTrackSelection(
        audioTracks = mutableListOf(),
        textTracks = mutableListOf()
) {

    val trackSelector = DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())
    private val trackNameProvider = DefaultTrackNameProvider(context.resources)

    private var _audioTracks: MutableList<String> = mutableListOf()
    private var _textTracks: MutableList<String> = mutableListOf()

    override val audioTracks: List<String> get() = _audioTracks
    override val textTracks: List<String> get() = _textTracks

    /** The track selector is linked to an Exo Player object and this way to an actual
     *  set of tracks available for a current playback.
     *
     *  This update synchronizes provided here a list of names of audio tracks and closed
     *  caption tracks with current set of tracks.
     */
    fun update() {
        _audioTracks.clear(); _textTracks.clear()
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return
        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
            val trackType = mappedTrackInfo.getRendererType(rendererIndex)
            if (trackType != C.TRACK_TYPE_AUDIO && trackType != C.TRACK_TYPE_TEXT) continue
            for (groupIndex in 0 until trackGroupArray.length) {
                val group = trackGroupArray[groupIndex]
                for (trackIndex in 0 until group.length) {
                    // skip non-language options
                    group.getFormat(trackIndex).language?: continue
                    // skip not handled format
                    if (mappedTrackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex)
                            != RendererCapabilities.FORMAT_HANDLED) continue
                    // add language option
                    val trackName = trackNameProvider.getTrackName(group.getFormat(trackIndex))
                    when (trackType) {
                        C.TRACK_TYPE_AUDIO -> _audioTracks.add(trackName)
                        C.TRACK_TYPE_TEXT -> _textTracks.add(trackName)
                    }
                }
            }
        }
    }
}