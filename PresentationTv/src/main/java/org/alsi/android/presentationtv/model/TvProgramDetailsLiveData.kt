package org.alsi.android.presentationtv.model

import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.session.TvBrowseCursor

class TvProgramDetailsLiveData {
    var cursor: TvBrowseCursor? = null
    var schedule: TvDaySchedule? = null
}