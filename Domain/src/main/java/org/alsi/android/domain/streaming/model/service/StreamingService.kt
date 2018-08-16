package org.alsi.android.domain.streaming.model.service

import org.alsi.android.domain.streaming.repository.DirectoryRepository
import org.alsi.android.domain.streaming.repository.SessionRepository

open class StreamingService (
        val id: Long,
        val kind: StreamingServiceKind,
        val tag: String,
        val directory: DirectoryRepository,
        val session: SessionRepository) {

    companion object {
        const val TV = "tv"
        const val VOD = "vod"
    }
}


