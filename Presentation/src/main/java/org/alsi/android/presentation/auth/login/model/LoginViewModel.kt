package org.alsi.android.presentation.auth.login.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import org.alsi.android.domain.context.interactor.StartSessionUseCase
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val startSessionUseCase: StartSessionUseCase,
    // use case to select session and server-stored language
) : ViewModel() {

    private val _liveData: MutableLiveData<Resource<Unit>> = MutableLiveData()

    val liveData: LiveData<Resource<Unit>> = _liveData

    fun login(loginName: String, loginPassword: String) {
        _liveData.postValue(Resource.loading())
        startSessionUseCase.execute(object: DisposableCompletableObserver() {
            override fun onComplete() {
                _liveData.postValue(Resource.success())
            }

            override fun onError(e: Throwable) {
                _liveData.postValue(Resource.error(e))
            }
        },
        StartSessionUseCase.Params(loginName, loginPassword))
    }
}