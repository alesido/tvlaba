package org.alsi.android.moidom

import org.alsi.android.domain.streaming.model.*
import org.alsi.android.moidom.repository.AccountDataServiceMoidom
import javax.inject.Inject
import javax.inject.Named

class MoidomServiceBuilder @Inject constructor(

        private val account: AccountDataServiceMoidom,

        @Named(Moidom.TV_DIRECTORY_REPOSITORY)
        private val tvDirectory: DirectoryRepository,

        @Named(Moidom.VOD_DIRECTORY_REPOSITORY)
        private val vodDirectory: DirectoryRepository,

        private val settings: SettingsRepository,
        private val device: DeviceDataRepository,
        private val session: SessionRepository) {

    fun buildTv(providerId: Long, serviceId: Long, serviceTag: String) = StreamingService(
            serviceId, providerId, StreamingServiceKind.TV, serviceTag,
            account, tvDirectory, settings, device, session)

    fun buildVod(providerId: Long, serviceId: Long, serviceTag: String)= StreamingService(
                serviceId, providerId, StreamingServiceKind.VOD, serviceTag,
                account, vodDirectory, settings, device, session)
}