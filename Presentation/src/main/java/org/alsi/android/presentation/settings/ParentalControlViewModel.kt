package org.alsi.android.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.streaming.interactor.ChangeParentalControlPasswordUseCase
import org.alsi.android.domain.streaming.interactor.DropSessionParentalControlPasswordUseCase
import org.alsi.android.domain.streaming.interactor.GetSessionParentalControlPasswordUseCase
import org.alsi.android.domain.streaming.interactor.SetSessionParentalControlPasswordUseCase
import org.alsi.android.domain.tv.interactor.guide.AuthorizeTvChannelAccessUseCase
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.presentation.settings.ParentalControlViewModel.ParentalEventKind.*
import org.alsi.android.presentation.state.Event
import javax.inject.Inject

class ParentalControlViewModel @Inject constructor (
    private val changeParentalControlPinUseCase: ChangeParentalControlPinUseCase
) : ViewModel() {

    private val liveData: MutableLiveData<Resource<Void>> = MutableLiveData()
    fun getLiveData(): LiveData<Resource<Void>> = liveData

    fun changePassword(currentPass: String, newPass: String) {
        serviceChannel.postValue(Event(ParentalServiceEventKind.LOADING))
        changePasswordUseCase.execute(object: DisposableCompletableObserver() {
            override fun onComplete() = serviceChannel.postValue(Event(
                ParentalServiceEventKind.REQUEST_SUCCESS))
            override fun onError(e: Throwable) = serviceChannel.postValue(Event(
                ParentalServiceEventKind.ERROR, error = e))
        }, ChangeParentalControlPasswordUseCase.Params(currentPass, newPass))
    }
}