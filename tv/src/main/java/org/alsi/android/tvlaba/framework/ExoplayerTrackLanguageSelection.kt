package org.alsi.android.tvlaba.framework

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider
import org.alsi.android.presentation.model.LanguageTrackSelection

class ExoplayerTrackLanguageSelection(context: Context): LanguageTrackSelection() {

    val trackSelector = DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())
    private val trackNameProvider = DefaultTrackNameProvider(context.resources)

    private var audioTrackReferences: MutableList<TrackReference> = mutableListOf()
    private var textTrackReferences: MutableList<TrackReference> = mutableListOf()

    override val audioTracks: List<String> get() = audioTrackReferences.map { it.title}
    override val textTracks: List<String> get() = textTrackReferences.map { it.title }

    /** The track selector is linked to an Exo Player object and this way to an actual
     *  set of tracks available for a current playback.
     *
     *  This update synchronizes provided here a list of names of audio tracks and closed
     *  caption tracks with current set of tracks.
     */
    override fun update() {
        audioTrackReferences.clear(); textTrackReferences.clear()
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return
        // loop over renderer types (video, audio, texts, metadata)
        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
            val trackType = mappedTrackInfo.getRendererType(rendererIndex)
            if (trackType != C.TRACK_TYPE_AUDIO && trackType != C.TRACK_TYPE_TEXT) continue
            // loop over renderer's track groups
            for (groupIndex in 0 until trackGroupArray.length) {
                val group = trackGroupArray[groupIndex]
                // loop over tracks in the group
                for (trackIndex in 0 until group.length) {
                    // skip tracks with not defined language (?)
                    val language = group.getFormat(trackIndex).language?: continue
                    // skip not handled formats
                    if (mappedTrackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex)
                            != C.FORMAT_HANDLED) continue
                    // track name
                    val trackName = trackNameProvider.getTrackName(group.getFormat(trackIndex))
                    // add language option with track reference
                    val trackReference = TrackReference(trackName, trackType, rendererIndex, groupIndex, trackIndex)
                    when (trackType) {
                        C.TRACK_TYPE_AUDIO -> {

                            audioTrackReferences.add(trackReference)

                            preferredLanguage?.let {
                                if (selectedAudioTrackIndex == 0 && language == it.code) {
                                    selectedAudioTrackIndex = audioTrackReferences.size
                                }
                            }
                        }
                        C.TRACK_TYPE_TEXT -> {

                            textTrackReferences.add(trackReference)

                            preferredLanguage?.let {
                                if (selectedTextTrackIndex == 0 && language == it.code) {
                                    selectedTextTrackIndex = textTrackReferences.size
                                }
                            }
                        }
                    }
                }
            }
        }
        // apply current track selection
        selectAudioTrack(selectedAudioTrackIndex)
        selectTextTrack(selectedTextTrackIndex)
    }

    /**
     * @param audioLanguageIndex Index of a subset of audio tracks defined locally to
     * present user with a selection menu. I.e., this is a subset of supported audio
     * tracks having a language defined.
     */
    override fun selectAudioTrack(audioLanguageIndex: Int) {
        selectedAudioTrackIndex = audioLanguageIndex
        if (selectedAudioTrackIndex < audioTrackReferences.size)
            selectTrackOverride(audioTrackReferences[audioLanguageIndex])
    }

    /**
     * @param textLanguageIndex Index of a subset of CC tracks defined locally to
     * present user with a selection menu. I.e., this is a subset of supported subtitles (text)
     * tracks having a language defined.
     */
    override fun selectTextTrack(textLanguageIndex: Int?) {
        selectedTextTrackIndex = textLanguageIndex
        selectedTextTrackIndex?.let {
            if (it < textTrackReferences.size)
                selectTrackOverride(textTrackReferences[it])
            return@selectTextTrack
        }
        turnSubtitlesOff()
    }

    private fun selectTrackOverride(trackReference: TrackReference) {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo
        val trackGroupArray = mappedTrackInfo?.getTrackGroups(trackReference.rendererIndex)?: return
       // trackGroupArray[trackReference.groupIndex]?: return // not sure for what this is

        val parametersBuilder = trackSelector.buildUponParameters()
        preferredLanguage?.let {
            parametersBuilder.setPreferredAudioLanguage(it.code)
        }

        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            if (mappedTrackInfo.getRendererType(rendererIndex) != trackReference.trackType)
                continue
            parametersBuilder.clearSelectionOverrides(rendererIndex)
            if (rendererIndex == trackReference.rendererIndex) {
                // set selected track to use
                parametersBuilder.setSelectionOverride(rendererIndex, trackGroupArray,
                        SelectionOverride(trackReference.groupIndex, trackReference.trackIndex))
                // make sure the renderer is enabled
                parametersBuilder.setRendererDisabled(rendererIndex, false)
            }
            else {
                // disable other renderers of the same type to avoid playback errors
                parametersBuilder.setRendererDisabled(rendererIndex, true)
            }
        }

        trackSelector.setParameters(parametersBuilder)
    }

    override fun turnSubtitlesOff() {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo?: return
        val parametersBuilder = trackSelector.buildUponParameters()
        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            if (mappedTrackInfo.getRendererType(rendererIndex) != C.TRACK_TYPE_TEXT)
                continue
            parametersBuilder.clearSelectionOverrides(rendererIndex)
            parametersBuilder.setRendererDisabled(rendererIndex, true)
        }
        trackSelector.setParameters(parametersBuilder)
    }
}

class TrackReference(
        val title: String,
        val trackType: Int,
        val rendererIndex: Int,
        val groupIndex: Int,
        val trackIndex: Int)