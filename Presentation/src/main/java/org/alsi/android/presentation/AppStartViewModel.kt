package org.alsi.android.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.context.interactor.TryResumeSessionUseCase
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.presentation.state.Resource
import org.alsi.android.presentation.state.ResourceState
import javax.inject.Inject

class AppStartViewModel @Inject constructor(

    private val tryResumeSessionUseCase: TryResumeSessionUseCase

) : ViewModel() {

    private val liveData: MutableLiveData<Resource<UserActivityRecord>> = MutableLiveData()

    init {
        tryResumeUserSession()
    }

    fun getNavigationTargetLiveData(): LiveData<Resource<UserActivityRecord>> = liveData

    private fun tryResumeUserSession() {
        liveData.postValue(Resource(ResourceState.LOADING, null, null))
        tryResumeSessionUseCase.execute(ResumeSessionSubscriber())
    }

    inner class ResumeSessionSubscriber: DisposableSingleObserver<UserActivityRecord>() {

        override fun onSuccess(t: UserActivityRecord) {
            liveData.postValue(Resource.success(t))
        }

        override fun onError(e: Throwable) {
            liveData.postValue(Resource.error(e))
        }
    }
}