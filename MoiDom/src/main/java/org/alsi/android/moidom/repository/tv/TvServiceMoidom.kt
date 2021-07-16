package org.alsi.android.moidom.repository.tv

import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.repository.SettingsRepositoryMoidom
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TvServiceMoidom @Inject constructor(

        @Named("${Moidom.TAG}.$TV") serviceId: Long,
        serviceDirectory: TvDirectoryRepositoryMoidom,
        playCursorRepository: TvPlayCursorRepositoryMoiDom,
        browseCursorRepository: TvBrowseCursorRepositoryMoiDom,
        configuration: SettingsRepositoryMoidom
    )

    : StreamingService(

        serviceId,
        kind = StreamingServiceKind.TV,
        tag = "${Moidom.TAG}.$TV",
        directory = serviceDirectory,
        session = TvSessionRepository(browseCursorRepository, playCursorRepository),
        configuration = configuration
    )