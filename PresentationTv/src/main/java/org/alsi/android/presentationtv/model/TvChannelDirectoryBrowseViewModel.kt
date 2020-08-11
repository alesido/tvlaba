package org.alsi.android.presentationtv.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import org.alsi.android.domain.context.interactor.StartSessionUseCase
import org.alsi.android.domain.tv.interactor.guide.TvChannelDirectoryObservationUseCase
import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import javax.inject.Inject

/** View model to allow user browse channel categories contents in, particularly, Leanback's
 *  media content browser layout.
 *
 */
open class TvChannelDirectoryBrowseViewModel @Inject constructor(
        private val startSessionUseCase: StartSessionUseCase,
        private val directoryObservationUseCase: TvChannelDirectoryObservationUseCase
)
    : ViewModel()
{
    private val liveData: MutableLiveData<Resource<TvChannelDirectory>> = MutableLiveData()

    init {
        fetchChannelDirectory()
    }

    fun getLiveData(): LiveData<Resource<TvChannelDirectory>> = liveData

    private fun fetchChannelDirectory() {
        liveData.postValue(Resource(ResourceState.LOADING, null, null))
        startSessionUseCase.execute(StartSessionSubscriber(),
                StartSessionUseCase.Params(
                        loginName = "51",
                        loginPassword = "123"
                ))
    }

    inner class StartSessionSubscriber: DisposableCompletableObserver() {
        override fun onComplete() {
            directoryObservationUseCase.execute(ChannelDirectorySubscriber())
        }
        override fun onError(e: Throwable) {
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }

    inner class ChannelDirectorySubscriber: DisposableObserver<TvChannelDirectory>() {
        override fun onNext(directory: TvChannelDirectory) {
            liveData.postValue(Resource(ResourceState.SUCCESS, directory, null))
        }

        override fun onComplete() {
            // not applicable
        }

        override fun onError(e: Throwable) {
            liveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }
}