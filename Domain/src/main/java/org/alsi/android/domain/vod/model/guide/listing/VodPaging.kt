package org.alsi.android.domain.vod.model.guide.listing

class VodListingPage (

    val sectionId: Long,
    val unitId: Long,

    val total: Int? = 0,
    val pageNumber: Int? = 1,
    val count: Int? = 0,

    val items: List<VodListingItem>,

    val timeStamp: Long? = null
) {
    fun isEmpty() = sectionId == -1L

    companion object {
        fun empty() = VodListingPage(-1L, -1L, items = listOf())
    }
}

class VodListingWindow (

    val sectionId: Long,
    val unitId: Long,

    val total: Int? = 0,
    val windowStart: Int? = 0,

    val items: List<VodListingItem>,

    val timeStamp: Long? = null
) {
    fun isEmpty() = sectionId == -1L

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20

        fun empty() = VodListingWindow(-1L, -1L, items = listOf())
    }
}