package org.alsi.android.presentationtv.model

import org.alsi.android.domain.tv.model.guide.TvWeekDayRange
import org.alsi.android.domain.tv.model.session.TvBrowseCursor

class TvProgramDetailsLiveData {
    var cursor: TvBrowseCursor? = null
    var weekDayRange: TvWeekDayRange? = null
}