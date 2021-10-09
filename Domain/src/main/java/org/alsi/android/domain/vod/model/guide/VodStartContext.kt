package org.alsi.android.domain.vod.model.guide

import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.tv.model.session.TvBrowseCursorReference
import org.alsi.android.domain.tv.model.session.TvPlayCursor
import org.alsi.android.domain.vod.model.session.VodBrowseCursor
import org.alsi.android.domain.vod.model.session.VodBrowseCursorReference
import org.alsi.android.domain.vod.model.session.VodPlayCursor

class VodStartContext (
    /**
     *  Last browsing cursor
     */
    val browse: VodBrowseCursorReference,

    /**
     *  Last playback cursor
     */
    val play: VodPlayCursor,

    /**
     * Last user activity
     */
    val activity: UserActivityRecord,
)