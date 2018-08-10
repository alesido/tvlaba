package org.alsi.android.domain.tv.repository.guide

import org.alsi.android.domain.streaming.model.DirectoryRepository

/**
 * Created on 7/18/18.
 */
open class TvDirectoryRepository (
        streamingServiceId: Long,
        val channels : TvChannelRepository,
        val programs: TvProgramRepository,
        val streams: TvVideoStreamRepository)
    : DirectoryRepository(streamingServiceId)