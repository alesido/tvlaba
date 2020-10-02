package org.alsi.android.domain.tv.model.guide

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat


/**  A part of a TV channels list visible to a user at the moment. Introduced to schedule updates
 * of live program data displayed within channel list item.
 */
class TvChannelListWindow (
      val ids: List<Long>,
      private val timeStampMillis: Long
) {
    override fun toString(): String {
        val timeFormatter = DateTimeFormat.forPattern("HH:mm:ss.SSS")
        return String.format("TV Channel List Window @%s, ids %s",
                DateTime(timeStampMillis).toString(timeFormatter),
                ids.joinToString (prefix = "{", separator = ",", postfix = "}"))
    }

    companion object {
        fun empty() = TvChannelListWindow(listOf(),0L)
    }
}