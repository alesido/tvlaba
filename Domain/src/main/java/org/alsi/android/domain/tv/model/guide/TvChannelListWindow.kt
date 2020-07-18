package org.alsi.android.domain.tv.model.guide


/** Continuous part of a TV channels list visible to user. Introduced to schedule updates
 * of live program data displayed within channel list item
 */
class TvChannelListWindow (
      val ids: List<Long>,
      val timeStampMillis: Long
)