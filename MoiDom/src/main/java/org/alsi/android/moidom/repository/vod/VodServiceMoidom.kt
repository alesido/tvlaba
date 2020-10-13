package org.alsi.android.moidom.repository.vod

import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.repository.SettingsRepositoryMoidom
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class VodServiceMoidom @Inject constructor(
        @Named("${Moidom.TAG}.${VOD}") serviceId: Long,
        configuration: SettingsRepositoryMoidom
) : StreamingService(
        serviceId,
        kind = StreamingServiceKind.TV,
        tag = "${Moidom.TAG}.${VOD}",
        directory = VodDirectoryRepositoryMoidom(),
        session = VodSessionRepositoryMoidom(),
        configuration = configuration
)