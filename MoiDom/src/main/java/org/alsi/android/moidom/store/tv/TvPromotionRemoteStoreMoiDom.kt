package org.alsi.android.moidom.store.tv

import org.alsi.android.datatv.store.TvPromotionRemoteStore
import org.alsi.android.moidom.mapper.TvPromotionSetSourceDataMapper
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom

class TvPromotionRemoteStoreMoiDom(
    private val remoteService: RestServiceMoidom,
    private val remoteSession: RemoteSessionRepositoryMoidom
): TvPromotionRemoteStore {

    val mapper = TvPromotionSetSourceDataMapper()

    override fun getPromotionSet() = remoteSession.getSessionId().flatMap {
        sid -> remoteService.getPromotionSet(sid)
    }.map {
        mapper.mapFromSource(it)
    }
}