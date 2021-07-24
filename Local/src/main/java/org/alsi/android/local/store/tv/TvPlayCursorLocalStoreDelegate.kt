package org.alsi.android.local.store.tv

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datatv.store.TvPlayCursorLocalStore
import org.alsi.android.domain.context.model.SessionActivityType
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.tv.model.session.TvPlayCursor
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.mapper.TvPlayCursorEntityMapper
import org.alsi.android.local.model.tv.TvPlayCursorEntity
import org.alsi.android.local.model.tv.TvPlayCursorEntity_
import org.alsi.android.local.model.tv.TvPlaybackEntity_

class TvPlayCursorLocalStoreDelegate (

        serviceBoxStore: BoxStore,
        val loginSubject: PublishSubject<UserAccount>

) : TvPlayCursorLocalStore
{
    private var userLoginName: String = "guest"

    private val cursorBox: Box<TvPlayCursorEntity> = serviceBoxStore.boxFor()

    private val playCursorMapper = TvPlayCursorEntityMapper()

    private val disposables = CompositeDisposable()

    init {
        val s = loginSubject.subscribe {
            switchUser(it.loginName)
        }
        s?.let { disposables.add(it) }
    }

    override fun switchUser(userLoginName: String) {
        this.userLoginName = userLoginName
    }

    /** Insert or update playback cursor record.
     *
     * Ensure we keep only a part of the accumulated playback history. I.e., this method triggers
     * history clean up procedure. It seems that it's better to remove records older than a day
     * or two than keep last N records.
     */
    override fun putPlayCursor(cursor: TvPlayCursor): Completable {
        return Completable.fromRunnable {

            // TODO Remove expired playback cursor records

            // find if a correspondent record exists
            // (check if the indexes work correct and there's no need to verify)
            val record = cursorBox.query {
                equal(TvPlayCursorEntity_.userLoginName, userLoginName)
                equal(TvPlayCursorEntity_.categoryId, cursor.categoryId)
                link(TvPlayCursorEntity_.playback)
                        .equal(TvPlaybackEntity_.programId, cursor.playback.programId?: 0L)
                        .equal(TvPlaybackEntity_.channelId, cursor.playback.channelId)
            }.findFirst()

            if (record != null) {
                with(record) {
                    seekTime = cursor.seekTime
                    timeStamp = System.currentTimeMillis()
                }
                cursorBox.put(record)
            }
            else {
                val entity = playCursorMapper.mapToEntity(cursor)
                entity.userLoginName = userLoginName
                cursorBox.put(entity)
            }
        }
    }

    override fun updatePlayCursor(currentPlayback: TvPlayback): Completable = Completable.fromCallable {
        val cursor = cursorBox.query {
            equal(TvPlayCursorEntity_.userLoginName, userLoginName)
            orderDesc(TvPlayCursorEntity_.timeStamp)
        }.findFirst()
        record?.let {
            record.seekTime = seekTime
            cursorBox.put(record)
        }
    }

    override fun getLastPlayCursor() = Single.fromCallable {
        val record = cursorBox.query {
            equal(TvPlayCursorEntity_.userLoginName, userLoginName)
            orderDesc(TvPlayCursorEntity_.timeStamp)
        }.findFirst()
        if (record != null) playCursorMapper.mapFromEntity(record) else TvPlayCursor.empty()
    }

    override fun getMostRecentActivity(serviceId: Long): Single<UserActivityRecord?> = Single.fromCallable {
        val record = cursorBox.query {
            orderDesc(TvPlayCursorEntity_.timeStamp)
        }.findFirst()
        if (record != null)
            UserActivityRecord(record.userLoginName, serviceId, SessionActivityType.PLAYBACK_TV, record.timeStamp)
        else
            UserActivityRecord.empty()
    }

    override fun getPlayHistory() = Single.fromCallable() {
        cursorBox.query {
            equal(TvPlayCursorEntity_.userLoginName, userLoginName)
            orderDesc(TvPlayCursorEntity_.timeStamp)
        }.find().map { playCursorMapper.mapFromEntity(it) }.toList()
    }


    fun dispose() {
        if (!disposables.isDisposed) disposables.dispose()
    }
}