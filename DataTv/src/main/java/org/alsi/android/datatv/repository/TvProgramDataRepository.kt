package org.alsi.android.datatv.repository

import io.reactivex.Single
import org.alsi.android.datatv.store.TvProgramLocalStore
import org.alsi.android.datatv.store.TvProgramRemoteStore
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.repository.guide.TvProgramRepository
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import javax.inject.Inject

open class TvProgramDataRepository @Inject constructor(

        protected val local: TvProgramLocalStore,
        protected val remote: TvProgramRemoteStore

): TvProgramRepository {

    override fun getDaySchedule(channelId: Long, date: LocalDate): Single<TvDaySchedule?> {
        return local.getDaySchedule(channelId, date).onErrorResumeNext {
            remote.getDaySchedule(channelId, date).flatMap {
                local.putDaySchedule(it)
                Single.just(it)
            }
        }
    }

    override fun getChannelLive(channelId: Long): Single<TvProgramIssue?> {
        return local.getChannelLive(channelId).onErrorResumeNext {
            remote.getDaySchedule(channelId, LocalDate.now()).flatMap {
                local.putDaySchedule(it)
                Single.just(it.live)
            }
        }
    }

    override fun getArchiveProgram(channelId: Long, dateTime: LocalDateTime): Single<TvProgramIssue?> {
        return local.getArchiveProgram(channelId, dateTime).onErrorResumeNext {
            remote.getDaySchedule(channelId, dateTime.toLocalDate()).flatMap {
                local.putDaySchedule(it)
                Single.just(it.programAtTime(dateTime))
            }
        }
    }
}