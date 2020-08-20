package org.alsi.android.domain.tv.model.guide

import org.joda.time.LocalDate
import java.util.*

class TvWeekDay(val date: LocalDate) {

    val monthDayString: String get() = String.format(Locale.getDefault(), "%s/%02d",
            date.dayOfMonth().asShortText, date.monthOfYear)

    val weekDayString: String get() {
        return try {
            date.dayOfWeek().getAsText(Locale.getDefault())
        } catch (x: Exception) {
            //  IllegalInstantException possible: Illegal instant due to time zone offset
            //  transition (daylight savings time 'gap'): 2018-03-11T02:17:12.599
            //  (America/Los_Angeles)
            ""
        }
    }
}