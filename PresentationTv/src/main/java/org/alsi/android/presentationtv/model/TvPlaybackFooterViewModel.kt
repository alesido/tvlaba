package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.tv.interactor.guide.TvBrowseCursorMoveUseCase
import org.alsi.android.domain.tv.interactor.guide.TvCurrentPlaybackUseCase
import org.alsi.android.domain.tv.interactor.guide.TvDayScheduleUseCase
import org.alsi.android.domain.tv.interactor.guide.TvWeekDayRangeUseCase
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvBrowsePage
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import javax.inject.Inject

class TvPlaybackFooterViewModel @Inject constructor (

        private val currentPlaybackUseCase: TvCurrentPlaybackUseCase,
        private val dayScheduleUseCase: TvDayScheduleUseCase,
        private val weekDayRangeUseCase: TvWeekDayRangeUseCase,
        private val browseCursorMoveUseCase: TvBrowseCursorMoveUseCase

): ViewModel() {

    private val liveData: MutableLiveData<Resource<TvPlaybackFooterLiveData>> = MutableLiveData()
    private var snapshot = TvPlaybackFooterLiveData()

    val currentScheduleItemPosition: Int get() = _currentScheduleItemPosition
    private var _currentScheduleItemPosition: Int = 0

    val selectedWeekDayPosition: Int get() = _selectedWeekDayPosition
    private var _selectedWeekDayPosition: Int = 0

    private var currentPlayback: TvPlayback? = null


    init {
        liveData.postValue(Resource(ResourceState.LOADING, null, null))
        weekDayRangeUseCase.execute(TvWeekDayRangeSubscriber())
        currentPlaybackUseCase.execute(CurrentPlaybackSubscriber())
    }

    fun getLiveData(): LiveData<Resource<TvPlaybackFooterLiveData>> = liveData

    fun dispose() {
        currentPlaybackUseCase.dispose()
        dayScheduleUseCase.dispose()
        weekDayRangeUseCase.dispose()
    }

    fun onTvWeekDayAction(weekDay: TvWeekDay) {
        snapshot.schedule?.let {
            dayScheduleUseCase.execute(DayScheduleSubscriber(), TvDayScheduleUseCase.Params(
                    channelId = it.channelId,
                    date = weekDay.date
            ))
        }
    }

    fun scheduleItemPositionOf(item: TvProgramIssue): Int?
            = snapshot.schedule?.positionOf(item)

    fun weekDayPositionOf(item: TvWeekDay): Int
            = snapshot.weekDayRange?.getWeekDayPosition(item.date)?: 0

    inner class CurrentPlaybackSubscriber: DisposableObserver<TvPlayback>() {
        override fun onNext(playback: TvPlayback) {
            currentPlayback = playback
            snapshot.schedule?.let {
                if (it.contains(playback)) {
                    moveToProgram(it, playback)
                    return
                }
            }
            dayScheduleUseCase.execute(DayScheduleSubscriber(), TvDayScheduleUseCase.Params(
                    channelId = playback.channelId,
                    date = playback.time?.startDateTime?.toLocalDate()
            ))
        }
        override fun onError(e: Throwable) {
            // handled in the playback view model
        }
        override fun onComplete() {
            // not applicable
        }
    }

    private fun moveToProgram(schedule: TvDaySchedule, playback: TvPlayback) {
        browseCursorMoveUseCase.execute(TvBrowseCursorMoveSubscriber(),
                TvBrowseCursorMoveUseCase.Params(
                        program = schedule.programForPlayback(playback),
                        page = TvBrowsePage.PLAYBACK,
                        reuse = true
                ))
    }

    private fun moveToScheduleAndProgram(schedule: TvDaySchedule, playback: TvPlayback) {
        browseCursorMoveUseCase.execute(TvBrowseCursorMoveSubscriber(),
                TvBrowseCursorMoveUseCase.Params(
                        schedule = schedule,
                        program = schedule.programForPlayback(playback),
                        page = TvBrowsePage.PLAYBACK,
                        reuse = true
                ))
    }

    inner class DayScheduleSubscriber: DisposableSingleObserver<TvDaySchedule>() {
        override fun onSuccess(schedule: TvDaySchedule) {
            snapshot.schedule = schedule
            currentPlayback?.let {
                _currentScheduleItemPosition = schedule.positionOf(it)?: schedule.middlePosition?: 0
                moveToScheduleAndProgram(schedule, currentPlayback!!)
            }
            snapshot.weekDayRange?.let {
                _selectedWeekDayPosition = it.getWeekDayPosition(schedule.date)?:0
            }
            liveData.postValue(Resource(ResourceState.SUCCESS, snapshot, null))
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }

    inner class TvWeekDayRangeSubscriber: DisposableSingleObserver<TvWeekDayRange>() {
        override fun onSuccess(t: TvWeekDayRange) {
            snapshot.weekDayRange = t
            snapshot.schedule?.let {
                _selectedWeekDayPosition = t.getWeekDayPosition(it.date)?: 0
            }
            liveData.postValue(Resource(ResourceState.SUCCESS, snapshot, null))
        }

        override fun onError(e: Throwable) {
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }

    inner class TvBrowseCursorMoveSubscriber: DisposableSingleObserver<TvBrowseCursor>() {
        override fun onSuccess(t: TvBrowseCursor) {
            // moving cursor emits cursor change event which is received in the cursor subscriber
        }
        override fun onError(e: Throwable) {
            // TODO Exit program details fragment upon error while getting schedule
            //  to complete browse cursor when just started from the channel directory
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }
}