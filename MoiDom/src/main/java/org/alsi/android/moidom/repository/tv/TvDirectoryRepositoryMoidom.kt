package org.alsi.android.moidom.repository.tv

import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository

class TvDirectoryRepositoryMoidom(moidomTvServiceId: Long): TvDirectoryRepository (
            streamingServiceId = moidomTvServiceId,
            channels = TvChannelDataRepositoryMoidom(),
            programs = TvProgramRepositoryMoidom(),
            streams = TvVideoStreamRepositoryMoidom())
