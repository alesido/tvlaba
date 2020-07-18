package org.alsi.android.moidom.repository.vod

import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named

class VodServiceMoidom @Inject constructor(
        @Named("${Moidom.TAG}.${StreamingService.VOD}") serviceId: Long)
    : StreamingService(
        serviceId,
        StreamingServiceKind.TV,
    "${Moidom.TAG}.${StreamingService.VOD}",
    VodDirectoryRepositoryMoidom(),
    VodSessionRepositoryMoidom())