package org.alsi.android.domain.tv.model.guide

import org.joda.time.DateTime
import java.util.*
import kotlin.math.roundToInt

class TvProgramTimeInterval(val startUnixTimeMillis: Long, val endUnixTimeMillis: Long) {

    val startDateTime = DateTime(startUnixTimeMillis)
    val endDateTime = DateTime(endUnixTimeMillis)

    val shortString: String? get() =
        if (startUnixTimeMillis != endUnixTimeMillis) {
            String.format("%02d:%02d - %02d:%02d",
                    startDateTime.hourOfDay, startDateTime.minuteOfHour,
                    endDateTime.hourOfDay, endDateTime.minuteOfHour)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d",
                    startDateTime.hourOfDay, startDateTime.minuteOfHour)
        }

    val progress: Int get() {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis <= startUnixTimeMillis) return 0
        return if (currentTimeMillis >= endUnixTimeMillis) 100
        else ((currentTimeMillis - startUnixTimeMillis).toFloat() * 100 /
                (endUnixTimeMillis - startUnixTimeMillis)).roundToInt()
    }

    val durationMillis: Long get() = endUnixTimeMillis - startUnixTimeMillis
}
