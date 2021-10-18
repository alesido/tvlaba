package org.alsi.android.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import org.alsi.android.domain.streaming.interactor.ChangeParentalControlPinUseCase
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class ParentalControlViewModel @Inject constructor (
    private val changeParentalControlPinUseCase: ChangeParentalControlPinUseCase
) : ViewModel() {

    private val liveData: MutableLiveData<Resource<Void>> = MutableLiveData()
    fun getLiveData(): LiveData<Resource<Void>> = liveData

    fun changeParentalControlPin(currentPin: String, newPin: String) {
        liveData.postValue(Resource.loading())
        changeParentalControlPinUseCase.execute(object: DisposableCompletableObserver() {
            override fun onComplete() = liveData.postValue(Resource.success())
            override fun onError(e: Throwable) = liveData.postValue(Resource.error(e))
        }, ChangeParentalControlPinUseCase.Params(currentPin, newPin))
    }
}