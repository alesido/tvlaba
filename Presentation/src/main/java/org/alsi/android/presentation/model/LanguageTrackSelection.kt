package org.alsi.android.presentation.model

import org.alsi.android.domain.streaming.model.options.LanguageOption

abstract class LanguageTrackSelection {

    abstract val audioTracks: List<String>
    abstract val textTracks: List<String>

    var preferredLanguage: LanguageOption? = null

    var selectedAudioTrackIndex = 0
    var selectedTextTrackIndex: Int? = null

    abstract fun update()

    abstract fun selectAudioTrack(audioLanguageIndex: Int)

    abstract fun selectTextTrack(textLanguageIndex: Int?)

    abstract fun turnSubtitlesOff()
}

