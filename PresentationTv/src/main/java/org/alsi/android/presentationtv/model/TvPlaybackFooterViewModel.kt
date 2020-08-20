package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.tv.interactor.guide.TvCurrentPlaybackUseCase
import org.alsi.android.domain.tv.interactor.guide.TvDayScheduleUseCase
import org.alsi.android.domain.tv.interactor.guide.TvWeekDayRangeUseCase
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvWeekDay
import org.alsi.android.domain.tv.model.guide.TvWeekDayRange
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import javax.inject.Inject

class TvPlaybackFooterViewModel @Inject constructor (

        private val currentPlaybackUseCase: TvCurrentPlaybackUseCase,
        private val dayScheduleUseCase: TvDayScheduleUseCase,
        private val weekDayRangeUseCase: TvWeekDayRangeUseCase

): ViewModel() {

    private val liveData: MutableLiveData<Resource<TvPlaybackFooterLiveData>> = MutableLiveData()
    private var snapshot = TvPlaybackFooterLiveData()

    val selectedWeekDayPosition: Int get() = _selectedWeekDayPosition
    private var _selectedWeekDayPosition: Int = 0


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

    fun weekDayPositionOf(item: TvWeekDay): Int
            = snapshot.weekDayRange?.getWeekDayPosition(item.date)?: 0

    inner class CurrentPlaybackSubscriber: DisposableObserver<TvPlayback>() {
        override fun onNext(t: TvPlayback) {
            dayScheduleUseCase.execute(DayScheduleSubscriber(), TvDayScheduleUseCase.Params(
                    channelId = t.channelId,
                    date = t.time?.startDateTime?.toLocalDate()
            ))
        }
        override fun onError(e: Throwable) {
            // handled in the playback view model
        }
        override fun onComplete() {
            // not applicable
        }
    }

    inner class DayScheduleSubscriber: DisposableSingleObserver<TvDaySchedule>() {
        override fun onSuccess(t: TvDaySchedule) {
            snapshot.schedule = t
            snapshot.weekDayRange?.let {
                _selectedWeekDayPosition = it.getWeekDayPosition(t.date)?:0
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
}