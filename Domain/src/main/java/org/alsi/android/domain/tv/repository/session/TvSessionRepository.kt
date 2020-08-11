package org.alsi.android.domain.tv.repository.session

import org.alsi.android.domain.streaming.repository.SessionRepository

class TvSessionRepository (
        val browse: TvBrowseRepository,
        val play: TvPlayCursorRepository

): SessionRepository()