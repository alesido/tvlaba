package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.tv.interactor.guide.*
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvBrowsePage
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import org.joda.time.LocalDate
import javax.inject.Inject

class TvProgramDetailsViewModel @Inject constructor (

        private val browseCursorObserveUseCase: TvBrowseCursorObserveUseCase,
        private val browseCursorMoveUseCase: TvBrowseCursorMoveUseCase,
        private val newPlaybackUseCase: TvNewPlaybackUseCase,
        private val dayScheduleUseCase: TvDayScheduleUseCase,
        private val weekDayRangeUseCase: TvWeekDayRangeUseCase

        ) : ViewModel() {

    private val liveData: MutableLiveData<Resource<TvProgramDetailsLiveData>> = MutableLiveData()
    private val snapshot = TvProgramDetailsLiveData()

    val currentScheduleItemPosition: Int get() = _currentScheduleItemPosition
    private var _currentScheduleItemPosition: Int = 0

    val selectedWeekDayPosition: Int get() = _selectedWeekDayPosition
    private var _selectedWeekDayPosition: Int = 0


    init {
        liveData.postValue(Resource(ResourceState.LOADING, null, null))
        browseCursorObserveUseCase.execute(TvBrowseCursorSubscriber())
        weekDayRangeUseCase.execute(TvWeekDayRangeSubscriber())
    }


    fun getLiveData(): LiveData<Resource<TvProgramDetailsLiveData>> = liveData

    fun onPlaybackAction(navigate: () -> Unit) {
        newPlaybackUseCase.execute(NewPlaybackSubscriber(navigate),
                with(snapshot.cursor!!) {
                    TvNewPlaybackUseCase.Params(category!!.id, channel, program)
                })
    }

    fun scheduleItemPositionOf(item: TvProgramIssue): Int?
            = snapshot.cursor?.schedule?.positionOf(item)

    fun weekDayPositionOf(item: TvWeekDay): Int? =
            snapshot.weekDayRange?.getWeekDayPosition(item.date)?: 0

    fun onTvProgramIssueAction(programIssue: TvProgramIssue) {
        snapshot.cursor?: return
        liveData.postValue(Resource(ResourceState.LOADING, null, null))
        with(snapshot.cursor!!) {
            browseCursorMoveUseCase.execute(TvBrowseCursorMoveSubscriber(),
                    TvBrowseCursorMoveUseCase.Params(
                            category = category,
                            channel = channel,
                            schedule = schedule,
                            program = programIssue,
                            page = TvBrowsePage.PROGRAM
                    ))
        }
    }

    fun onWeekDayAction(weekDay: TvWeekDay) {
        snapshot.cursor?: return
        with (snapshot.cursor!!) {
            if (schedule?.date == weekDay.date) return
            liveData.postValue(Resource(ResourceState.LOADING, null, null))
            dayScheduleUseCase.execute(DayScheduleSubscriber(), TvDayScheduleUseCase.Params(
                    channelId = channel!!.id,
                    date = weekDay.date
            ))
        }
    }

    fun dispose() {
        browseCursorObserveUseCase.dispose()
        browseCursorMoveUseCase.dispose()
        newPlaybackUseCase.dispose()
        dayScheduleUseCase.dispose()
        weekDayRangeUseCase.dispose()
    }

    inner class TvBrowseCursorSubscriber: DisposableObserver<TvBrowseCursor>() {
        override fun onNext(cursor: TvBrowseCursor) {
            if (null == cursor.category && null == cursor.channel) return
            snapshot.cursor = cursor
            if (cursor.schedule != null && cursor.program != null) {
                // another program selected from the current week day
                liveData.postValue(Resource(ResourceState.SUCCESS, snapshot, null))
                if (cursor.schedule != null) {
                    _currentScheduleItemPosition = cursor.schedule!!.
                        positionOf(cursor.program!!)?: 0
                    _selectedWeekDayPosition = snapshot.weekDayRange?.
                        getWeekDayPosition(cursor.schedule!!.date)?: 0
                }
            }
            else if (cursor.schedule != null && null == cursor.program) {
                // another week day selected ...
                with(cursor) {
                    browseCursorMoveUseCase.execute(TvBrowseCursorMoveSubscriber(),
                            TvBrowseCursorMoveUseCase.Params(
                                    category = category,
                                    channel = channel,
                                    schedule = schedule,
                                    program = schedule?.programAtMiddle,
                                    page = TvBrowsePage.PROGRAM
                            ))
                }
            }
            else {
                // came from the channel directory - both current schedule and program is N/A
                dayScheduleUseCase.execute(DayScheduleSubscriber(), TvDayScheduleUseCase.Params(
                        channelId = cursor.channel!!.id,
                        date = LocalDate.now()
                ))
            }
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
        override fun onComplete() { /* not applicable */ }
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

    inner class NewPlaybackSubscriber (val navigate: () -> Unit)
        : DisposableSingleObserver<TvPlayback>() {
        override fun onSuccess(t: TvPlayback) {
            navigate()
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }

    inner class DayScheduleSubscriber: DisposableSingleObserver<TvDaySchedule>() {
        override fun onSuccess(schedule: TvDaySchedule) {
            with(snapshot.cursor!!) {
                browseCursorMoveUseCase.execute(TvBrowseCursorMoveSubscriber(),
                    TvBrowseCursorMoveUseCase.Params(
                            category = category,
                            channel = channel,
                            schedule = schedule,
                            program = schedule.live?: schedule.programAtMiddle, // "live" is null for an earlier or a later week day
                            page = TvBrowsePage.PROGRAM
                    ))
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
            val browseDate = snapshot.cursor?.schedule?.date?: LocalDate.now()
            _selectedWeekDayPosition = t.getWeekDayPosition(browseDate)?: 0
            if (snapshot.cursor?.program != null)
                liveData.postValue(Resource(ResourceState.SUCCESS, snapshot, null))
        }

        override fun onError(e: Throwable) {
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }
}