package org.alsi.android.moidom.mapper

import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
import org.alsi.android.moidom.model.vod.VodListResponse
import org.alsi.android.remote.mapper.SourceDataMapper
import java.net.URI

class VodListingPageMapper: SourceDataMapper<VodListResponse, VodListingPage> {

    fun mapFromSource(sectionId: Long, unitId: Long, start: Int, source: VodListResponse)
    : VodListingPage =
        VodListingPage(sectionId, unitId, source.total, start,
            source.vods.map { VodListingItem(
                it.id, sectionId, unitId,
                it.name, it.description,
                posters = VodListingItem.Posters(URI.create(it.image_url))
            )}
        )

    override fun mapFromSource(source: VodListResponse): VodListingPage {
        TODO("Not implemented. Use another method.")
    }
}