package org.alsi.android.domain.tv.model.guide

import java.net.URI

class TvChannel (

        val id: Long,
        val categoryId: Long,
        val logoUri: URI,
        val number: Int,
        val title: String) {

    var live: TvProgramIssue? = null

    var hasArchive: Boolean = true
    var hasSchedule: Boolean = true
    var isPasswordProtected: Boolean = false
    var hasMultipleLanguageAudioTracks: Boolean = false
}
