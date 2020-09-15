package org.alsi.android.domain.tv.model.guide


/**  A part of a TV channels list visible to a user at the moment. Introduced to schedule updates
 * of live program data displayed within channel list item.
 */
class TvChannelListWindow (
      val ids: List<Long>,
      val timeStampMillis: Long
)