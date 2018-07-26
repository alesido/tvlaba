package org.alsi.android.domain.tv.model.session

import org.alsi.android.domain.implementation.model.ListCursor
import org.joda.time.LocalDate

/**
 * Created on 7/15/18.
 */
class TvBrowseCursor(
        var category: ListCursor<Long>,
        var channel: ListCursor<Long>,
        var program: ListCursor<Long>,
        var date: LocalDate
)