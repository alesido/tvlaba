package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.tv.interactor.guide.TvBrowseCursorMoveUseCase
import org.alsi.android.domain.tv.interactor.guide.TvBrowseCursorObserveUseCase
import org.alsi.android.domain.tv.interactor.guide.TvDayScheduleUseCase
import org.alsi.android.domain.tv.interactor.guide.TvNewPlaybackUseCase
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvBrowsePage
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import javax.inject.Inject

class TvProgramDetailsViewModel @Inject constructor (

        private val browseCursorObserveUseCase: TvBrowseCursorObserveUseCase,
        private val browseCursorMoveUseCase: TvBrowseCursorMoveUseCase,
        private val dayScheduleUseCase: TvDayScheduleUseCase,
        private val newPlaybackUseCase: TvNewPlaybackUseCase

) : ViewModel() {

    private val liveData: MutableLiveData<Resource<TvProgramDetailsLiveData>> = MutableLiveData()
    private val snapshot = TvProgramDetailsLiveData()

    val currentScheduleItemPosition: Int get() = _currentScheduleItemPosition
    private var _currentScheduleItemPosition: Int = 0

    init {
        liveData.postValue(Resource(ResourceState.LOADING, null, null))
        browseCursorObserveUseCase.execute(TvBrowseCursorSubscriber())
    }


    fun getLiveData(): LiveData<Resource<TvProgramDetailsLiveData>> = liveData

    fun onPlaybackAction(navigate: () -> Unit) {
        newPlaybackUseCase.execute(NewPlaybackSubscriber(navigate),
                with(snapshot.cursor!!) {
                    TvNewPlaybackUseCase.Params(category!!.id, channel, program)
                })
    }

    fun scheduleItemPositionOf(item: TvProgramIssue): Int? = snapshot.schedule?.positionOf(item)

    fun onTvProgramIssueAction(item: TvProgramIssue) {
        snapshot.cursor?: return
        liveData.postValue(Resource(ResourceState.LOADING, null, null))
        with(snapshot.cursor!!) {
            browseCursorMoveUseCase.execute(TvBrowseCursorMoveSubscriber(),
                    TvBrowseCursorMoveUseCase.Params(
                            category = category,
                            channel = channel,
                            schedule = schedule,
                            program = item,
                            page = TvBrowsePage.PROGRAM
                    ))
        }
    }

    fun dispose() {
        browseCursorObserveUseCase.dispose()
        dayScheduleUseCase.dispose()
    }

    inner class TvBrowseCursorSubscriber: DisposableObserver<TvBrowseCursor>() {
        override fun onNext(cursor: TvBrowseCursor) {
            if (null == cursor.category && null == cursor.channel) return
            snapshot.cursor = cursor
            if (cursor.program != null) {
                liveData.postValue(Resource(ResourceState.SUCCESS, snapshot, null))
                if (snapshot.schedule != null) {
                    _currentScheduleItemPosition = snapshot.schedule!!.positionOf(cursor.program!!)?: 0
                }
            }
            else {
                dayScheduleUseCase.execute(DayScheduleSubscriber(), TvDayScheduleUseCase.Params(
                        channelId = cursor.channel!!.id,
                        date = cursor.channel!!.live.time?.startDateTime?.toLocalDate()
                ))
                // TODO Get current program with designated use case instead of getting schedule
                //  - some API may have a method to get a program's details
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

    inner class DayScheduleSubscriber: DisposableSingleObserver<TvDaySchedule>() {
        override fun onSuccess(schedule: TvDaySchedule) {
            snapshot.schedule = schedule
            if (null == snapshot.cursor!!.program) {
                with(snapshot.cursor!!) {
                    browseCursorMoveUseCase.execute(TvBrowseCursorMoveSubscriber(),
                        TvBrowseCursorMoveUseCase.Params(
                                category = category,
                                channel = channel,
                                schedule = schedule,
                                program = schedule.live,
                                page = TvBrowsePage.PROGRAM
                        ))
                }
            }
            liveData.postValue(Resource(ResourceState.SUCCESS, snapshot, null))
        }
        override fun onError(e: Throwable) {
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
}