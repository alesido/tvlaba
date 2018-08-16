package org.alsi.android.moidom.repository.tv

import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named

class TvDirectoryRepositoryMoidom @Inject constructor(
        @Named("${Moidom.TAG}.${StreamingService.TV}") moidomTvServiceId: Long)
    : TvDirectoryRepository(
            moidomTvServiceId,
            TvChannelDataRepositoryMoidom(moidomTvServiceId),
            TvProgramRepositoryMoidom(),
            TvVideoStreamRepositoryMoidom())
