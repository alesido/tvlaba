package org.alsi.android.domain.tv.model.guide

import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.tv.model.session.TvBrowseCursorReference
import org.alsi.android.domain.tv.model.session.TvPlayCursor

class TvStartContext (

    /**
     *  Last browsing cursor
     */
    val browse: TvBrowseCursorReference,

    /**
     *  Last playback cursor
     */
    val play: TvPlayCursor,

    /**
     * Last user activity
     */
    val activity: UserActivityRecord
)