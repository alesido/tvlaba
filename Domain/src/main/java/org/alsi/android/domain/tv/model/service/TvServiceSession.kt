package org.alsi.android.domain.tv.model.service

import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvPlayCursor

/**
 * Created on 7/18/18.
 */
class TvServiceSession(
        val id: Long,
        val browse: TvBrowseCursor,
        val play: TvPlayCursor,
        val parentCode: String)