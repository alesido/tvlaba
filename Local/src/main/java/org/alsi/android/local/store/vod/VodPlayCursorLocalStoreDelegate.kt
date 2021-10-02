package org.alsi.android.local.store.vod

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.alsi.android.datavod.store.VodPlayCursorLocalStore
import org.alsi.android.domain.context.model.SessionActivityType
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.domain.vod.model.guide.playback.VodPlayback
import org.alsi.android.domain.vod.model.session.VodPlayCursor
import org.alsi.android.local.mapper.vod.VodPlayCursorEntityMapper
import org.alsi.android.local.mapper.vod.VodPlaybackEntityMapper
import org.alsi.android.local.model.tv.TvPlayCursorEntity_
import org.alsi.android.local.model.vod.VodPlayCursorEntity
import org.alsi.android.local.model.vod.VodPlayCursorEntity_
import org.alsi.android.local.model.vod.VodPlaybackEntity
import org.alsi.android.local.model.vod.VodPlaybackEntity_

class VodPlayCursorLocalStoreDelegate (
    serviceBoxStore: BoxStore,
    val loginSubject: PublishSubject<UserAccount>
) : VodPlayCursorLocalStore {

    private var userLoginName: String = "guest"

    private val cursorBox: Box<VodPlayCursorEntity> = serviceBoxStore.boxFor()

    private val playCursorMapper = VodPlayCursorEntityMapper()
    private val playbackMapper = VodPlaybackEntityMapper()

    private val disposables = CompositeDisposable()

    init {
        loginSubject.subscribe { switchUser(it.loginName) }?.let { disposables.add(it) }
    }

    override fun switchUser(userLoginName: String) {
        this.userLoginName = userLoginName
    }

    override fun putPlayCursor(cursor: VodPlayCursor) = Completable.fromRunnable {
        val record = cursorBox.query {
            equal(VodPlayCursorEntity_.userLoginName, userLoginName)
            link(VodPlayCursorEntity_.playback)
                .equal(VodPlaybackEntity_.sectionId, cursor.playback.sectionId)
                .equal(VodPlaybackEntity_.unitId, cursor.playback.unitId)
                .equal(VodPlaybackEntity_.itemId, cursor.playback.itemId)
                .equal(VodPlaybackEntity_.seriesId, cursor.playback.seriesId?: 0L)
        }.findFirst()
        if (record != null) {
            record.seekTime = cursor.seekTime
            record.timeStamp = cursor.timeStamp
            cursorBox.put(record)
        }
        else {
            val entity = playCursorMapper.mapToEntity(cursor)
            entity.userLoginName = userLoginName
            cursorBox.put(entity)
        }

    }

    override fun updatePlayCursor(currentPlayback: VodPlayback) = Completable.fromCallable {
        val cursor = cursorBox.query {
            equal(VodPlayCursorEntity_.userLoginName, userLoginName)
            orderDesc(VodPlayCursorEntity_.timeStamp)
        }.findFirst()
        cursor?.let {
            cursor.seekTime = currentPlayback.position
            cursor.playback.target = playbackMapper.mapToEntity(currentPlayback)
            cursorBox.put(cursor)
        }
    }

    override fun getLastPlayCursor(): Single<VodPlayCursor?> = Single.fromCallable {
        val record = cursorBox.query {
            equal(VodPlayCursorEntity_.userLoginName, userLoginName)
            orderDesc(VodPlayCursorEntity_.timeStamp)
        }.findFirst()
        record?.let {
            val resultCursor = playCursorMapper.mapFromEntity(record)
            resultCursor.playback.position = resultCursor.seekTime
            resultCursor
        }
    }

    override fun getMostRecentActivity(serviceId: Long): Single<UserActivityRecord?> = Single.fromCallable {
        val record = cursorBox.query {
            orderDesc(VodPlayCursorEntity_.timeStamp)
        }.findFirst()
        if (record != null)
            UserActivityRecord(record.userLoginName, serviceId, SessionActivityType.PLAYBACK_VOD, record.timeStamp)
        else
            UserActivityRecord.empty()

    }

    override fun getPlayHistory(): Single<List<VodPlayCursor>?> = Single.fromCallable {
        cursorBox.query {
            equal(VodPlayCursorEntity_.userLoginName, userLoginName)
            orderDesc(VodPlayCursorEntity_.timeStamp)
        }.find().map { playCursorMapper.mapFromEntity(it) }.toList()
    }
}