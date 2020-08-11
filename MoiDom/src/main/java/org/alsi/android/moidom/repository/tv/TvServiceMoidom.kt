package org.alsi.android.moidom.repository.tv

import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.streaming.repository.SessionRepository
import org.alsi.android.domain.tv.repository.session.TvBrowseRepository
import org.alsi.android.domain.tv.repository.session.TvPlayCursorRepository
import org.alsi.android.domain.tv.repository.session.TvSessionRepository
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named

class TvServiceMoidom @Inject constructor(

        @Named("${Moidom.TAG}.$TV") serviceId: Long,
        serviceDirectory: TvDirectoryRepositoryMoidom,
        playCursorRepository: TvPlayCursorRepositoryMoiDom
    )

    : StreamingService(

        serviceId,
        kind = StreamingServiceKind.TV,
        tag = "${Moidom.TAG}.$TV",
        directory = serviceDirectory,
        session = TvSessionRepository(TvBrowseRepository(), playCursorRepository))