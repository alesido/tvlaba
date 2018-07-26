package org.alsi.android.domain.vod.model.session

import org.alsi.android.domain.implementation.model.ListCursor


/**
 * Created on 7/15/18.
 */
class VodBrowseCursor(
        var section: ListCursor<Long>,
        var unit: ListCursor<Long>,
        var item: ListCursor<Long>,
        var part: ListCursor<Long>
)