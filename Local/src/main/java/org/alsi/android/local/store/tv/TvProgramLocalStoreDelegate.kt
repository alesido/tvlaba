package org.alsi.android.local.store.tv

import android.content.Context
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.datatv.store.TvProgramLocalStore
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.local.mapper.tv.TvProgramIssueEntityMapper
import org.alsi.android.local.model.tv.TvProgramIssueEntity
import org.alsi.android.local.model.tv.TvProgramIssueEntity_
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import javax.inject.Inject

class TvProgramLocalStoreDelegate(

        serviceBoxStore: BoxStore,
        private var userLoginName: String = "guest"

) : TvProgramLocalStore {

    val mapper = TvProgramIssueEntityMapper()

    @Inject
    lateinit var context: Context

    private val box: Box<TvProgramIssueEntity> = serviceBoxStore.boxFor()

    override fun switchUser(userLoginName: String) {
        this.userLoginName = userLoginName
    }

    override fun putDaySchedule(daySchedule: TvDaySchedule) = Completable.fromRunnable {
        box.put(daySchedule.items.map { mapper.mapToEntity(it) })
    }

    override fun getDaySchedule(channelId: Long, date: LocalDate) = Single.fromCallable {
        val s = date.toDateTimeAtStartOfDay().millis
        val e = date.plusDays(1).toDateTimeAtStartOfDay().millis

        val innerItems: List<TvProgramIssueEntity> = box.query {
            equal(TvProgramIssueEntity_.channelId, channelId)
            greater(TvProgramIssueEntity_.startMillis, s - 1L)
            less(TvProgramIssueEntity_.startMillis, e + 1L)
            order(TvProgramIssueEntity_.startMillis)
        }.find()

        val borderItem = box.query {
            equal(TvProgramIssueEntity_.channelId, channelId)
            less(TvProgramIssueEntity_.startMillis, s + 1L)
            greater(TvProgramIssueEntity_.endMillis, s - 1L)
            order(TvProgramIssueEntity_.startMillis)
        }.findFirst()

        val items = mutableListOf<TvProgramIssueEntity>()
        borderItem?.let { items[0] = it }
        items.addAll(innerItems)

        TvDaySchedule(channelId = channelId, date = date,
                items = items.map { mapper.mapFromEntity(it)})
    }

    override fun getChannelLive(channelId: Long) = Single.fromCallable {
        val now = System.currentTimeMillis()
        val item = box.query {
            equal(TvProgramIssueEntity_.channelId, channelId)
            less(TvProgramIssueEntity_.startMillis, now + 1L)
            greater(TvProgramIssueEntity_.endMillis, now - 1L)
            order(TvProgramIssueEntity_.startMillis)
        }.findFirst()
        item?.let { mapper.mapFromEntity(item) }
    }

    override fun getArchiveProgram(channelId: Long, dateTime: LocalDateTime) = Single.fromCallable {
        val dt = dateTime.toDateTime().millis
        val item = box.query {
            equal(TvProgramIssueEntity_.channelId, channelId)
            less(TvProgramIssueEntity_.startMillis, dt + 1L)
            greater(TvProgramIssueEntity_.endMillis, dt - 1L)
            order(TvProgramIssueEntity_.startMillis)
        }.findFirst()
        item?.let {mapper.mapFromEntity(item) }
    }

    override fun compactStorage() {
        TODO("Not yet implemented")
    }
}