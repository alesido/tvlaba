package org.alsi.android.presentationtv.model

class TrackSelection(val audioTracks: List<String>, val textTracks: List<String>) {
    companion object {
        fun empty() = TrackSelection(listOf(), listOf())
    }
}

