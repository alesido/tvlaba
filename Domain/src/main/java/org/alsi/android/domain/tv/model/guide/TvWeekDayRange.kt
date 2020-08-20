@file:Suppress("MemberVisibilityCanBePrivate")

package org.alsi.android.domain.tv.model.guide

import org.joda.time.DateTimeConstants
import org.joda.time.Days
import org.joda.time.LocalDate

class TvWeekDayRange {

    val firstDay: LocalDate
    val lastDay: LocalDate

    private val _weekDays: MutableList<TvWeekDay> = mutableListOf()
    val weekDays: List<TvWeekDay> = _weekDays // read only access

    private var totalRangeWeeks = 0


    /** Default program week days range ending this sunday.
     *
     * @param totalRangeWeeks Total weeks in the range ending this Sunday.
     * @param timeShiftMillis Time shift option.
     */
    constructor(totalRangeWeeks: Int, vararg timeShiftMillis: Long) {
        this.totalRangeWeeks = totalRangeWeeks
        val now = LocalDate(System.currentTimeMillis() -
                if (timeShiftMillis.isNotEmpty()) timeShiftMillis[0] else 0)
        lastDay = now.withDayOfWeek(DateTimeConstants.SUNDAY)
        firstDay = lastDay.minusDays(this.totalRangeWeeks * 7 - 1) // Monday of 1st day of the 4 weeks period
        for (i in 0 until this.totalRangeWeeks * 7) {
            _weekDays.add(TvWeekDay(firstDay.plusDays(i)))
        }
    }

    /** A program week day range starting with given 1st day and ending on given last day.
     *
     * @param firstDay 1st day of a program week days range.
     * @param lastDay Last
     */
    constructor(firstDay: LocalDate, lastDay: LocalDate) {
        val totalDays = Days.daysBetween(firstDay, lastDay).days
        totalRangeWeeks = totalDays / 7
        this.firstDay = firstDay
        this.lastDay = lastDay
        for (i in 0 until totalDays) {
            _weekDays.add(TvWeekDay(this.firstDay.plusDays(i)))
        }
    }

    fun getWeekDayPosition(weekDayDate: LocalDate): Int? {
        var i = 0
        val s = _weekDays.size
        while (i < s) {
            if (_weekDays[i].date.compareTo(weekDayDate) == 0) return i
            i++
        }
        return null
    }

    fun findWeekDay(weekDayDate: LocalDate): TvWeekDay? {
        var i = 0
        val s = _weekDays.size
        while (i < s) {
            val wd: TvWeekDay = _weekDays[i]
            if (wd.date.compareTo(weekDayDate) == 0) return wd
            i++
        }
        return null
    }

    operator fun contains(weekDayDate: LocalDate?): Boolean {
        return !(null == weekDayDate || weekDayDate.isBefore(firstDay) || weekDayDate.isAfter(lastDay))
    }
}