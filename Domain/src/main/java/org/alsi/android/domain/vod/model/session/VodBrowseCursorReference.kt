package org.alsi.android.domain.vod.model.session

import org.joda.time.LocalDate

/**
 *  Reference to browsing cursor for VOD introduced to postpone expensive
 *  operation of populating browsing cursor.
 */
data class VodBrowseCursorReference (
    val sectionId: Long,
    val unitId: Long,
    val itemId: Long,
    val itemPosition: Int? = null,

    val page: VodBrowsePage,

    val timeStamp: Long
) {
    fun isEmpty() = sectionId == -1L

    companion object {
        fun empty() = VodBrowseCursorReference(-1L, -1L, -1L,
            -1, VodBrowsePage.UNKNOWN, -1L)
    }
}