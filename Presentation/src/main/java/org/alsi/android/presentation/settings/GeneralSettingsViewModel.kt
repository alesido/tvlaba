package org.alsi.android.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import org.alsi.android.domain.context.model.ServicePresentationType
import org.alsi.android.domain.streaming.interactor.SelectStreamingServerUseCase
import org.alsi.android.domain.streaming.interactor.StreamingProfileUseCase
import org.alsi.android.domain.streaming.interactor.StreamingSettingsUseCase
import org.alsi.android.domain.streaming.model.service.StreamingServiceProfile
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class GeneralSettingsViewModel @Inject constructor(

    private val valuesUseCase: StreamingSettingsUseCase,
    private val profileUseCase: StreamingProfileUseCase,
    private val selectServerUseCase: SelectStreamingServerUseCase

) : ViewModel() {

    private val liveSettingValues: MutableLiveData<Resource<StreamingServiceSettings>> = MutableLiveData()
    private val liveSettingsProfile: MutableLiveData<Resource<StreamingServiceProfile>> = MutableLiveData()

    // TODO Define streaming service to which settings view model belongs (try base it on current context)
    var servicePresentationType: ServicePresentationType = ServicePresentationType.TV_GUIDE

    fun getLiveSettingValues(): LiveData<Resource<StreamingServiceSettings>> = liveSettingValues
    fun getLiveSettingsProfile(): LiveData<Resource<StreamingServiceProfile>> = liveSettingsProfile

    init {
        liveSettingValues.postValue(Resource.loading())
        valuesUseCase.execute(ValuesSubscriber())
        profileUseCase.execute(ProfileSubscriber())
    }

    fun selectStreamingServer(serverTag: String) {
        selectServerUseCase.execute(SelectSettingSubscriber(),
            SelectStreamingServerUseCase.Params(serverTag))
    }

    fun selectStreamingServiceLanguage(languageCode: String) {

    }

    fun dispose() {
        valuesUseCase.dispose()
        profileUseCase.dispose()
        selectServerUseCase.dispose()
    }

    inner class ValuesSubscriber : DisposableObserver<StreamingServiceSettings>() {
        override fun onNext(settingValues: StreamingServiceSettings) {
            liveSettingValues.postValue(Resource.success(settingValues))
        }
        override fun onComplete() { /** not applicable */ }
        override fun onError(e: Throwable) = liveSettingValues.postValue(Resource.error(e))
    }

    inner class ProfileSubscriber()
        : DisposableObserver<StreamingServiceProfile>() {
        override fun onNext(settingsProfile: StreamingServiceProfile) {
            liveSettingsProfile.postValue(Resource.success(settingsProfile))
        }
        override fun onComplete() { /** not applicable */ }
        override fun onError(e: Throwable) = liveSettingValues.postValue(Resource.error(e))
    }

    inner class SelectSettingSubscriber : DisposableCompletableObserver() {
        override fun onComplete() { /** correspondent observer will be called */ }
        override fun onError(e: Throwable) = liveSettingValues.postValue(Resource.error(e))
    }
}