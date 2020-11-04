package org.alsi.android.presentationtv.model

open class LanguageTrackSelection(
        open val audioTracks: List<String>,
        open val textTracks: List<String>
) {
    companion object {
        fun empty() = LanguageTrackSelection(listOf(), listOf())
    }
}

