package org.alsi.android.presentation.auth.login.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.context.interactor.LastSessionAccountUseCase
import org.alsi.android.domain.context.interactor.StartSessionUseCase
import org.alsi.android.domain.streaming.interactor.GetStreamingSettingsUseCase
import org.alsi.android.domain.streaming.interactor.SelectLanguageUseCase
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.domain.user.SetRememberMeAtLoginUseCase
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.presentation.state.Resource
import java.util.*
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val startSessionUseCase: StartSessionUseCase,
    private val lastSessionAccountUseCase: LastSessionAccountUseCase,
    private val settingsUseCase: GetStreamingSettingsUseCase,
    private val setRememberMeUseCase: SetRememberMeAtLoginUseCase,
    private val selectLanguageUseCase: SelectLanguageUseCase,
) : ViewModel() {

    private val _liveData: MutableLiveData<Resource<Unit>> = MutableLiveData()
    val liveData: LiveData<Resource<Unit>> = _liveData

    private val _liveAccount: MutableLiveData<Resource<UserAccount>> = MutableLiveData()
    val liveAccount: LiveData<Resource<UserAccount>> = _liveAccount

    fun login(loginName: String, loginPassword: String, rememberMe: Boolean) {
        _liveData.postValue(Resource.loading())
        startSessionUseCase.execute(object: DisposableCompletableObserver() {
            override fun onComplete() {
                settingsUseCase.execute(SettingsSubscriber { settings ->
                    val userSelectedLanguage = Locale.getDefault().language
                    if (userSelectedLanguage != settings.language?.code) {
                        selectLanguageUseCase.execute(SelectLanguageSubscriber {
                            setRememberMeUseCase.execute(SetRememberMeSubscriber(),
                                SetRememberMeAtLoginUseCase.Params(rememberMe))
                        }, SelectLanguageUseCase.Params(userSelectedLanguage))
                    }
                    else {
                        setRememberMeUseCase.execute(SetRememberMeSubscriber(),
                            SetRememberMeAtLoginUseCase.Params(rememberMe))

                    }
                })
            }
            override fun onError(e: Throwable) = _liveData.postValue(Resource.error(e))
        },
        StartSessionUseCase.Params(loginName, loginPassword))
    }

    fun lastSessionAccount() {
        _liveAccount.postValue(Resource.loading())
        lastSessionAccountUseCase.execute(object: DisposableSingleObserver<UserAccount>() {
            override fun onSuccess(t: UserAccount) = _liveAccount.postValue(Resource.success(t))
            override fun onError(e: Throwable)  = _liveAccount.postValue(Resource.error(e))
        })
    }

    inner class SettingsSubscriber(
        val callBack: (settingValues: StreamingServiceSettings) -> Unit
    ) : DisposableSingleObserver<StreamingServiceSettings>() {
        override fun onSuccess(settingValues: StreamingServiceSettings) =
            callBack(settingValues)
        override fun onError(e: Throwable) = _liveData.postValue(Resource.error(e))
    }

    inner class SelectLanguageSubscriber(val callBack: () -> Unit) : DisposableCompletableObserver() {
        override fun onComplete() =
            callBack()
        override fun onError(e: Throwable) = _liveData.postValue(Resource.error(e))
    }

    inner class SetRememberMeSubscriber() : DisposableCompletableObserver() {
        override fun onComplete() =
            _liveData.postValue(Resource.success())
        override fun onError(e: Throwable) = _liveData.postValue(Resource.error(e))
    }
}