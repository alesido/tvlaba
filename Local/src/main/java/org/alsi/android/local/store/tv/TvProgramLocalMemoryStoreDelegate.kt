package org.alsi.android.local.store.tv

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.datatv.store.TvProgramLocalStore
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

class TvProgramLocalMemoryStoreDelegate: TvProgramLocalStore {

    var store: MutableMap<Long, MutableMap<Long, TvDaySchedule>> = mutableMapOf()

    override fun switchUser(userLoginName: String) {
        store = mutableMapOf()
    }

    override fun putDaySchedule(daySchedule: TvDaySchedule) = Completable.fromRunnable {
        with(daySchedule) {
            val dayKey: Long = date.toDateTimeAtStartOfDay().millis
            if (!store.contains(dayKey)) store[dayKey] = mutableMapOf()
            store[dayKey]!![channelId] = daySchedule
        }
    }

    override fun getDaySchedule(channelId: Long, date: LocalDate) = Single.fromCallable {
        val dayKey: Long = date.toDateTimeAtStartOfDay().millis
        store[dayKey]?: return@fromCallable null
        return@fromCallable store[dayKey]!![channelId]
    }

    override fun getChannelLive(channelId: Long) = Single.fromCallable {
        val dayKey: Long = LocalDate.now().toDateTimeAtStartOfDay().millis
        store[dayKey]?: return@fromCallable null
        store[dayKey]!![channelId]?: return@fromCallable null
        store[dayKey]!![channelId]?.live
    }

    override fun getArchiveProgram(channelId: Long, dateTime: LocalDateTime) = Single.fromCallable {
        val dayKey: Long = dateTime.toLocalDate().toDateTimeAtStartOfDay().millis
        store[dayKey]?: return@fromCallable null
        store[dayKey]!![channelId]?: return@fromCallable null
        store[dayKey]!![channelId]?.programAtTime(dateTime)
    }

    override fun compactStorage() {
        store = mutableMapOf()
    }
}