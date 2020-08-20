package org.alsi.android.presentationtv.model

import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvWeekDayRange

class TvPlaybackFooterLiveData(
        var schedule: TvDaySchedule? = null,
        var weekDayRange: TvWeekDayRange? = null
)