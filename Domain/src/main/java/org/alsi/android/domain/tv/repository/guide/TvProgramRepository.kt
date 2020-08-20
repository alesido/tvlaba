package org.alsi.android.domain.tv.repository.guide

import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.model.guide.TvWeekDayRange
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

/**
 *
 *
 * Created on 7/3/18.
 */
interface TvProgramRepository {

    fun getDaySchedule(channelId: Long, date: LocalDate): Single<TvDaySchedule?>

    fun getChannelLive(channelId: Long): Single<TvProgramIssue?>
    fun getArchiveProgram(channelId: Long, dateTime: LocalDateTime): Single<TvProgramIssue?>

    fun getScheduleWeekDayRange() : Single<TvWeekDayRange>
}