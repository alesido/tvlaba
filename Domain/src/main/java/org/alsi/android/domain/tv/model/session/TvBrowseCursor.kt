package org.alsi.android.domain.tv.model.session

import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvProgramIssue

/** Current position in the TV Channel and Program Catalog maintained to:
 *
 *  - provide full set of data to present current catalog browsing position in the UI
 *  (e.g. to provide channel name and program title in a program details page);
 *
 *  - to restore last session browsing position upon the app restart.
 *
 *  Supposedly, the stored entity will contain ID's, not links.
 */
data class TvBrowseCursor (
    val category: TvChannelCategory? = null,
    val channel: TvChannel? = null,
    val schedule: TvDaySchedule? = null,
    val program: TvProgramIssue? = null,
    val page: TvBrowsePage? = null,
    val timeStamp: Long = System.currentTimeMillis()
)

enum class TvBrowsePage {
    UNKNOWN,
    CATEGORIES, // categories list in channel a directory or a standalone category selection screen
    CHANNELS,
    SCHEDULE,   // standalone page, or in program details related, or in a playback footer
    PROGRAM,
    PLAYBACK
 }