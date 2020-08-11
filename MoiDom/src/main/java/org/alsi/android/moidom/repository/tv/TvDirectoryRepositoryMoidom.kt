package org.alsi.android.moidom.repository.tv


import org.alsi.android.datatv.repository.TvVideoStreamDataRepository
import org.alsi.android.domain.streaming.model.service.StreamingService.Companion.TV
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named

class TvDirectoryRepositoryMoidom @Inject constructor(
        @Named("${Moidom.TAG}.$TV") serviceId: Long,
        channelsRepository: TvChannelDataRepositoryMoidom,
        streamsRepository: TvVideoStreamDataRepository

    )
    : TvDirectoryRepository(
            streamingServiceId = serviceId,
            channels = channelsRepository,
            programs = TvProgramRepositoryMoidom(),
            streams = streamsRepository)
