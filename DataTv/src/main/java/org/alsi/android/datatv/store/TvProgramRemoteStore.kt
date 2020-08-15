package org.alsi.android.datatv.store

import io.reactivex.Single
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.joda.time.LocalDate

interface TvProgramRemoteStore {

    fun getDaySchedule(channelId: Long, date: LocalDate): Single<TvDaySchedule>
}