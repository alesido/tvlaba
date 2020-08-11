package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableObserver
import org.alsi.android.domain.tv.interactor.guide.TvCurrentPlaybackUseCase
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import javax.inject.Inject

class TvPlaybackViewModel @Inject constructor(

        private val currentPlaybackUseCase: TvCurrentPlaybackUseCase

) : ViewModel() {

    val liveData: MutableLiveData<Resource<TvPlayback>> = MutableLiveData()

    init {
        liveData.postValue(Resource(ResourceState.LOADING, null, null))
        currentPlaybackUseCase.execute(CurrentPlaybackSubscriber())
    }

    fun getLiveData(): LiveData<Resource<TvPlayback>> = liveData

    fun dispose() {
        currentPlaybackUseCase.dispose()
    }

    inner class CurrentPlaybackSubscriber: DisposableObserver<TvPlayback>() {
        override fun onNext(t: TvPlayback) {
            liveData.postValue(Resource(ResourceState.SUCCESS, t, null))
        }
        override fun onComplete() {
            // seems not applicable
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }
}