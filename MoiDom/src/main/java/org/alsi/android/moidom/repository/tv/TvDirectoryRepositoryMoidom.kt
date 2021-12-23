package org.alsi.android.moidom.repository.tv


import org.alsi.android.datatv.repository.TvProgramDataRepository
import org.alsi.android.datatv.repository.TvPromotionDataRepository
import org.alsi.android.datatv.repository.TvVideoStreamDataRepository
import org.alsi.android.domain.streaming.model.service.StreamingService.Companion.TV
import org.alsi.android.domain.tv.repository.guide.TvDirectoryRepository
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TvDirectoryRepositoryMoidom @Inject constructor(
        @Named("${Moidom.TAG}.$TV") serviceId: Long,
        channelRepository: TvChannelDataRepositoryMoidom,
        programRepository: TvProgramDataRepository,
        streamRepository: TvVideoStreamDataRepository,
        promotionsRepository: TvPromotionDataRepository
    )
    : TvDirectoryRepository(
            streamingServiceId = serviceId,
            channels = channelRepository,
            programs = programRepository,
            streams = streamRepository,
            promotions = promotionsRepository
    )
