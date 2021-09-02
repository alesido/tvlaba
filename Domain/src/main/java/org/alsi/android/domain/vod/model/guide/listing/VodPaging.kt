package org.alsi.android.domain.vod.model.guide.listing

class VodListingPage (

    val sectionId: Long,
    val unitId: Long,

    val total: Int,
    val pageNumber: Int,
    val count: Int,

    val items: List<VodListingItem>
)

class VodListingWindow (

    val sectionId: Int,
    val unitId: Int,

    val total: Int,
    val windowStart: Int,

    val items: List<VodListingItem>
) {
    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20
    }
}