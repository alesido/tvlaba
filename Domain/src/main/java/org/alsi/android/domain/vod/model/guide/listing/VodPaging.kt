package org.alsi.android.domain.vod.model.guide.listing

class VodListingPage (

    val sectionId: Long,
    val unitId: Long,

    /**
     *  Total number of items in a unit's listing.
     */
    val total: Int? = null,

    /**
     *  Start index of page, continuous sublist of a unit's listing.
     */
    val start: Int = 0,

    /**
     *  List of page items
     */
    val items: List<VodListingItem>,

    /**
     *  Time stamp in millis of the moment when this page was received from a data provider
     */
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

    var total: Int? = null, // total items in the listing, window size = items list size

    var start: Int = 0,
    val items: MutableList<VodListingItem>,

    val timeStamp: Long? = null
) {
    constructor(page: VodListingPage) : this(
        page.sectionId, page.unitId, page.total?:0,
        page.start,
        page.items.toMutableList(),
        page.timeStamp
    )

    fun add(page: VodListingPage): Boolean {
        if (page.sectionId != sectionId || page.unitId != unitId)
            return false

        if (items.isEmpty()) {
            items.addAll(page.items)
            start = page.start
            total = page.total // in case it's changed
            return true
        }

        val afterWindowEnd = start + items.size

        if (page.start == afterWindowEnd) {
            // added page is immediately after current window
            items.addAll(page.items)
            total = page.total // in case it's changed
            return true
        }

        if (page.start > afterWindowEnd)
            return false // listing window may not have discontinuities

        if (page.start + page.items.size == start) {
            // added page is immediately before the window
            start = page.start
            items.addAll(0, page.items)
            total = page.total // in case it's changed
            return true
        }

        return false
    }


    fun isEmpty() = sectionId == -1L || items.isEmpty()
    fun isNotEmpty() = sectionId != -1L && items.isNotEmpty()

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20

        fun empty() = VodListingWindow(-1L, -1L, items = mutableListOf())
    }
}