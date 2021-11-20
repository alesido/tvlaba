package org.alsi.android.presentation.auth.login.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import org.alsi.android.domain.context.interactor.StartSessionUseCase
import org.alsi.android.domain.streaming.interactor.GetStreamingSettingsUseCase
import org.alsi.android.domain.streaming.interactor.SelectLanguageUseCase
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.presentation.state.Resource
import java.util.*
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val startSessionUseCase: StartSessionUseCase,
    private val settingsUseCase: GetStreamingSettingsUseCase,
    private val selectLanguageUseCase: SelectLanguageUseCase,
) : ViewModel() {

    private val _liveData: MutableLiveData<Resource<Unit>> = MutableLiveData()

    val liveData: LiveData<Resource<Unit>> = _liveData

    fun login(loginName: String, loginPassword: String, rememberMe: Boolean) {
        _liveData.postValue(Resource.loading())
        startSessionUseCase.execute(object: DisposableCompletableObserver() {
            override fun onComplete() {
                settingsUseCase.execute(SettingsSubscriber { settings ->
                    val userSelectedLanguage = Locale.getDefault().language
                    if (userSelectedLanguage != settings.language?.code) {
                        selectLanguageUseCase.execute(SelectLanguageSubscriber {
                            _liveData.postValue(Resource.success())
                        }, SelectLanguageUseCase.Params(userSelectedLanguage))
                    }
                    else {
                        _liveData.postValue(Resource.success())
                    }
                })
            }
            override fun onError(e: Throwable) = _liveData.postValue(Resource.error(e))
        },
        StartSessionUseCase.Params(loginName, loginPassword))
    }
    inner class SettingsSubscriber(
        val callBack: (settingValues: StreamingServiceSettings) -> Unit
    ) : DisposableSingleObserver<StreamingServiceSettings>() {
        override fun onSuccess(settingValues: StreamingServiceSettings) = callBack(settingValues)
        override fun onError(e: Throwable) = _liveData.postValue(Resource.error(e))
    }

    inner class SelectLanguageSubscriber(val callBack: () -> Unit) : DisposableCompletableObserver() {
        override fun onComplete() = callBack()
        override fun onError(e: Throwable) = _liveData.postValue(Resource.error(e))
    }

}