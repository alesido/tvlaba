package org.alsi.android.domain.tv.model.guide

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*
import kotlin.math.roundToInt

class TvProgramTimeInterval(val startUnixTimeMillis: Long, val endUnixTimeMillis: Long) {

    val startDateTime = DateTime(startUnixTimeMillis)
    val endDateTime = DateTime(endUnixTimeMillis)

    val isCurrent: Boolean get() {
        val current = System.currentTimeMillis()
        return current in startUnixTimeMillis..endUnixTimeMillis
    }

    fun contains(instantMillis: Long) = instantMillis in startUnixTimeMillis..endUnixTimeMillis

    fun isBefore(instantMillis: Long) = endUnixTimeMillis <= instantMillis

    val isNotSet get() = startUnixTimeMillis < 0 || startUnixTimeMillis == endUnixTimeMillis

    val shortString: String get() =
        if (startUnixTimeMillis != endUnixTimeMillis) {
            String.format("%02d:%02d - %02d:%02d",
                    startDateTime.hourOfDay, startDateTime.minuteOfHour,
                    endDateTime.hourOfDay, endDateTime.minuteOfHour)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d",
                    startDateTime.hourOfDay, startDateTime.minuteOfHour)
        }

    override fun toString(): String {
        val dateFormat = DateTimeFormat.shortDate()
        return when {
            startUnixTimeMillis == endUnixTimeMillis -> {
                String.format("%s %02d:%02d",
                        startDateTime.toString(dateFormat),
                        startDateTime.hourOfDay, startDateTime.minuteOfHour)
            }
            startDateTime.toLocalDate() == endDateTime.toLocalDate() -> {
                String.format("%s %02d:%02d - %02d:%02d",
                        startDateTime.toString(dateFormat),
                        startDateTime.hourOfDay, startDateTime.minuteOfHour,
                        endDateTime.hourOfDay, endDateTime.minuteOfHour)
            }
            else -> {
                String.format("%s %02d:%02d - %s %02d:%02d",
                        startDateTime.toString(dateFormat),
                        startDateTime.hourOfDay, startDateTime.minuteOfHour,
                        endDateTime.toString(dateFormat),
                        endDateTime.hourOfDay, endDateTime.minuteOfHour)
            }
        }
    }

    fun shift(hours: Int): TvProgramTimeInterval = TvProgramTimeInterval(
        startDateTime.plusHours(hours).millis, endDateTime.plusHours(hours).millis)

    val progress: Int get() {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis <= startUnixTimeMillis) return 0
        return if (currentTimeMillis >= endUnixTimeMillis) 100
        else ((currentTimeMillis - startUnixTimeMillis).toFloat() * 100 /
                (endUnixTimeMillis - startUnixTimeMillis)).roundToInt()
    }

    val durationMillis: Long get() = endUnixTimeMillis - startUnixTimeMillis
}
