package org.alsi.android.domain.tv.model.guide

import org.joda.time.LocalDate

/**
 *  Day schedule of TV programs.
 */
class TvDaySchedule(val channelId: Long, val date: LocalDate, val timeShiftHours: Int, val items: List<TvProgramIssue>)