package org.alsi.android.moidom.store.tv

import org.alsi.android.datatv.store.TvProgramRemoteStore
import org.alsi.android.moidom.mapper.TvDayScheduleSourceDataMapper
import org.alsi.android.moidom.repository.RemoteSessionRepositoryMoidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

open class TvProgramRemoteStoreMoidom @Inject constructor(

        private val remoteService: RestServiceMoidom,
        private val remoteSession: RemoteSessionRepositoryMoidom

) : TvProgramRemoteStore {

    private val dateFormatProgramDate = DateTimeFormat.forPattern("ddMMyy")

    private val mapper = TvDayScheduleSourceDataMapper()

    override fun getDaySchedule(channelId: Long, date: LocalDate) = remoteSession.getSessionId()
            .flatMap { sid -> remoteService.getChannelSchedule (
                    sid = sid,
                    channelId = channelId.toString(),
                    programDayDateString = date.toString(dateFormatProgramDate),
                    timeZone = getTimeZoneQueryParameter(),
                    withTimeShift = 0)
            }.map {  scheduleSource ->
                mapper.mapFromSource(channelId, date, scheduleSource)
            }

    private fun getTimeZoneQueryParameter(): String {
        val tzOffsetHours = TimeUnit.MILLISECONDS.toHours( DateTimeZone.getDefault()
                .getOffset(System.currentTimeMillis()).toLong() )
        val sb = StringBuilder()
        sb.append(if (tzOffsetHours > 0) "+" else "-")
        val tzOffsetHoursAbs = abs(tzOffsetHours).toInt()
        if (tzOffsetHoursAbs < 10) sb.append(0)
        sb.append(tzOffsetHoursAbs).append("00")
        return sb.toString()
    }
}