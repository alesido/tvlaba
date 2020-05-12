package org.alsi.android.moidom.repository.tv

import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.streaming.repository.SessionRepository
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named

class TvServiceMoidom @Inject constructor(

        @Named("${Moidom.TAG}.$TV") serviceId: Long,
        serviceDirectory: TvDirectoryRepositoryMoidom
    )

    : StreamingService(

        serviceId,
        kind = StreamingServiceKind.TV,
        tag = "${Moidom.TAG}.$TV",
        directory = serviceDirectory,
        session = SessionRepository())