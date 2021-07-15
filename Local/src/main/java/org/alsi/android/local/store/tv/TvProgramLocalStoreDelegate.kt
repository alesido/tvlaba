package org.alsi.android.local.store.tv

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.alsi.android.datatv.store.TvProgramLocalStore
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.local.mapper.tv.TvProgramIssueEntityMapper
import org.alsi.android.local.model.tv.TvProgramIssueEntity
import org.alsi.android.local.model.tv.TvProgramIssueEntity_
import org.alsi.android.local.model.user.UserAccountSubject
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

class TvProgramLocalStoreDelegate(

        serviceBoxStore: BoxStore,
        accountSubject: UserAccountSubject

) : TvProgramLocalStore {

    private var userLoginName: String = "guest"

    val mapper = TvProgramIssueEntityMapper()

    private val box: Box<TvProgramIssueEntity> = serviceBoxStore.boxFor()

    private val disposables = CompositeDisposable()

    init {
        val s = accountSubject.subscribe {
            switchUser(it.loginName)
        }
        s?.let { disposables.add(it) }
    }

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

    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }
}