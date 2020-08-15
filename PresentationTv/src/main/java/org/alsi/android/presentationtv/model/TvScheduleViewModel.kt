package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.tv.interactor.guide.TvCurrentPlaybackUseCase
import org.alsi.android.domain.tv.interactor.guide.TvDayScheduleUseCase
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import javax.inject.Inject

class TvScheduleViewModel @Inject constructor (

        private val currentPlaybackUseCase: TvCurrentPlaybackUseCase,
        private val dayScheduleUseCase: TvDayScheduleUseCase

): ViewModel() {

    private val liveData: MutableLiveData<Resource<TvDaySchedule>> = MutableLiveData()

    init {
        liveData.postValue(Resource(ResourceState.LOADING, null, null))
        currentPlaybackUseCase.execute(CurrentPlaybackSubscriber())
    }

    fun getLiveData(): LiveData<Resource<TvDaySchedule>> = liveData

    fun dispose() {
        currentPlaybackUseCase.dispose()
        dayScheduleUseCase.dispose()
    }

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
            liveData.postValue(Resource(ResourceState.SUCCESS, t, null))
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }
}