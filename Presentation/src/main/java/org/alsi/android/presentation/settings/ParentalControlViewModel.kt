package org.alsi.android.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.streaming.interactor.ChangeParentalControlPasswordUseCase
import org.alsi.android.domain.streaming.interactor.DropSessionParentalControlPasswordUseCase
import org.alsi.android.domain.streaming.interactor.GetSessionParentalControlPasswordUseCase
import org.alsi.android.domain.tv.interactor.guide.AuthorizeTvChannelAccessUseCase
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvPlayback
import org.alsi.android.presentation.settings.ParentalControlViewModel.ParentalEventKind.ACCESS_GRANTED
import org.alsi.android.presentation.state.Event
import javax.inject.Inject

class ParentalControlViewModel @Inject constructor (
    private val authorizeContentUseCase: AuthorizeTvChannelAccessUseCase,
    private val changePasswordUseCase: ChangeParentalControlPasswordUseCase,
    getSessionPasswordUseCase: GetSessionParentalControlPasswordUseCase,
    private val dropSessionPasswordUseCase: DropSessionParentalControlPasswordUseCase
) : ViewModel() {

    private val eventChannel: MutableLiveData<Event<ParentalEventKind>> = MutableLiveData()
    private val serviceChannel: MutableLiveData<Event<ParentalServiceEventKind>> = MutableLiveData()

    private var currentPass: String? = null

    private var channelVerified: TvChannel? = null
    private var playbackVerified: TvPlayback? = null

    init {
        getSessionPasswordUseCase.execute(object: DisposableSingleObserver<String>() {
            override fun onSuccess(t: String) {
                 if (t.isNotEmpty()) currentPass = t
            }
            override fun onError(e: Throwable) {
                currentPass = null
            }
        })
    }

    fun getEventChannel(): LiveData<Event<ParentalEventKind>> = eventChannel
    fun getServiceEventChannel(): LiveData<Event<ParentalServiceEventKind>> = serviceChannel

    fun authorizeAccessWith(pass: String) {
        serviceChannel.postValue(Event(ParentalServiceEventKind.LOADING))
        authorizeContentUseCase.execute(AuthorizationSubscriber(pass),
            AuthorizeTvChannelAccessUseCase.Params(pass))
    }

    fun changePassword(currentPass: String, newPass: String) {
        serviceChannel.postValue(Event(ParentalServiceEventKind.LOADING))
        changePasswordUseCase.execute(object: DisposableCompletableObserver() {
            override fun onComplete() = serviceChannel.postValue(Event(
                ParentalServiceEventKind.REQUEST_SUCCESS))
            override fun onError(e: Throwable) = serviceChannel.postValue(Event(
                ParentalServiceEventKind.ERROR, error = e))
        }, ChangeParentalControlPasswordUseCase.Params(currentPass, newPass))
    }

    private fun invalidatePassword() {
         dropSessionPasswordUseCase.execute(object: DisposableCompletableObserver() {
             override fun onComplete() { currentPass = null }
             override fun onError(e: Throwable) {}
         })
    }

    fun isAccessAllowed(playback: TvPlayback): Boolean {
        if (playback.isUnderParentControl != true) {
            // business rule: request PIN when back to protected channel
            invalidatePassword()
            return true
        }
        playbackVerified = playback
        return currentPass != null

    }

    fun isAccessAllowed(channel: TvChannel): Boolean {
        if (!channel.features.isPasswordProtected) {
            // business rule: request PIN when back to protected channel
            invalidatePassword()
            return true
        }
        channelVerified = channel
        return currentPass != null
    }

    inner class AuthorizationSubscriber(private val pass: String): DisposableCompletableObserver() {
        override fun onComplete() {
            currentPass = pass
            serviceChannel.postValue(Event(ParentalServiceEventKind.REQUEST_SUCCESS))
            eventChannel.postValue(Event(ACCESS_GRANTED, payload = channelVerified))
            channelVerified = null
        }
        override fun onError(e: Throwable) = serviceChannel.postValue(
            Event(ParentalServiceEventKind.ERROR, error = e))
    }

    /** "Exported" events
     */
    enum class ParentalEventKind {
        ACCESS_GRANTED, ERROR
    }

    /** "Internal", service events
     */
    enum class ParentalServiceEventKind {
        LOADING, REQUEST_SUCCESS, ERROR
    }
}