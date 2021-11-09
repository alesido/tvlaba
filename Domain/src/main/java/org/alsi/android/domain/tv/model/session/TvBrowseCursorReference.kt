package org.alsi.android.domain.tv.model.session

import org.joda.time.LocalDate

/**
 *  Reference to browsing cursor for TV introduced to postpone expensive operation
 *  to populate full browsing cursor.
 */
data class TvBrowseCursorReference (
    val categoryId: Long,
    val channelId: Long,

    val scheduleDate: LocalDate?,
    val programId: Long?,

    val page: TvBrowsePage,

    val timeStamp: Long
) {
    fun isEmpty() = categoryId == -1L

    fun isMenuItemReference(): Boolean = (categoryId == 0L && channelId == 0L)

    companion object {
        fun empty() = TvBrowseCursorReference(-1L, -1L, null,
            -1L, TvBrowsePage.UNKNOWN, -1L)
    }
}