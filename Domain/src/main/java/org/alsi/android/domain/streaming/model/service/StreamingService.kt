package org.alsi.android.domain.streaming.model.service

import org.alsi.android.domain.streaming.repository.DirectoryRepository
import org.alsi.android.domain.streaming.repository.SessionRepository
import org.alsi.android.domain.streaming.repository.SettingsRepository

open class StreamingService (
        val id: Long,
        val kind: StreamingServiceKind,
        val tag: String,
        val configuration: SettingsRepository,
        val directory: DirectoryRepository,
        val session: SessionRepository,
        val presentation: StreamingServicePresentation
) {
    companion object {
        const val TV = "tv"
        const val VOD = "vod"
        const val DEFAULT_TV_ID = 1L
        const val DEFAULT_VOD_ID = 2L
    }
}


