package org.alsi.android.datatv.store

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

interface TvProgramLocalStore {

    fun switchUser(userLoginName: String)

    fun putDaySchedule(daySchedule:  TvDaySchedule): Completable
    fun getDaySchedule(channelId: Long, date: LocalDate): Single<TvDaySchedule?>

    fun getChannelLive(channelId: Long): Single<TvProgramIssue?>
    fun getArchiveProgram(channelId: Long, dateTime: LocalDateTime): Single<TvProgramIssue?>

    fun compactStorage() // remove expired program cache
}