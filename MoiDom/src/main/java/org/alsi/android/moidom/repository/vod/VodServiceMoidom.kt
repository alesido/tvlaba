package org.alsi.android.moidom.repository.vod

import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.vod.model.session.VodSessionRepository
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.repository.SettingsRepositoryMoidom
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class VodServiceMoidom @Inject constructor(
        @Named("${Moidom.TAG}.${VOD}") serviceId: Long,
        serviceDirectory: VodDirectoryRepositoryMoidom,
        browseCursorRepository: VodBrowseCursorRepositoryMoiDom,
        playCursorRepository: VodPlayCursorRepositoryMoiDom,
        configuration: SettingsRepositoryMoidom
) : StreamingService(
        serviceId,
        kind = StreamingServiceKind.VOD,
        tag = "${Moidom.TAG}.${VOD}",
        directory = serviceDirectory,
        session = VodSessionRepository(browseCursorRepository, playCursorRepository),
        configuration = configuration
)