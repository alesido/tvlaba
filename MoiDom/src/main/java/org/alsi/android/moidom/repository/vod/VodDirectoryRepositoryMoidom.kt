package org.alsi.android.moidom.repository.vod

import org.alsi.android.datavod.repository.VodDataRepository
import org.alsi.android.datavod.store.VodDirectoryLocalStore
import org.alsi.android.datavod.store.VodDirectoryRemoteStore
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named

class VodDirectoryRepositoryMoidom @Inject constructor(
    @Named("${Moidom.TAG}.${StreamingService.VOD}") serviceId: Long,
    @Named("${Moidom.TAG}.${StreamingService.VOD}") remoteStore: VodDirectoryRemoteStore,
    @Named("${Moidom.TAG}.${StreamingService.VOD}") localStore: VodDirectoryLocalStore
): VodDataRepository(serviceId, remoteStore, localStore) {

    override fun onLanguageChange(): Completable {
        TODO("Not yet implemented")
    }

    override fun onTimeShiftChange(): Completable {
        TODO("Not yet implemented")
    }
}