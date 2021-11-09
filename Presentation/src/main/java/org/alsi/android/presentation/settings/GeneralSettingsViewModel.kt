package org.alsi.android.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableObserver
import org.alsi.android.domain.streaming.interactor.*
import org.alsi.android.domain.streaming.model.service.StreamingServiceProfile
import org.alsi.android.domain.streaming.model.service.StreamingServiceSettings
import org.alsi.android.presentation.settings.GeneralSettingsEventKind.LANGUAGE_CHANGED
import org.alsi.android.presentation.state.Event
import org.alsi.android.presentation.state.Resource
import javax.inject.Inject

class GeneralSettingsViewModel @Inject constructor(

    valuesUseCase: StreamingSettingsUseCase,
    profileUseCase: StreamingProfileUseCase,
    private val selectLanguageUseCase: SelectLanguageUseCase,
    private val selectServerUseCase: SelectStreamingServerUseCase,
    private val selectCacheSizeUseCase: SelectCacheSizeUseCase,
    private val selectStreamBitrateUseCase: SelectStreamBitrateUseCase,
    private val selectDeviceModelUseCase: SelectDeviceModelUseCase,

    ) : ViewModel() {

    private val liveSettingValues: MutableLiveData<Resource<StreamingServiceSettings>> = MutableLiveData()
    private val liveSettingsProfile: MutableLiveData<Resource<StreamingServiceProfile>> = MutableLiveData()
    private val eventChannel: MutableLiveData<Event<GeneralSettingsEventKind>> = MutableLiveData()

    fun getLiveSettingValues(): LiveData<Resource<StreamingServiceSettings>> = liveSettingValues
    fun getLiveSettingsProfile(): LiveData<Resource<StreamingServiceProfile>> = liveSettingsProfile
    fun getEventChannel(): LiveData<Event<GeneralSettingsEventKind>> = eventChannel

    init {
        liveSettingValues.postValue(Resource.loading())
        valuesUseCase.execute(ValuesSubscriber())
        profileUseCase.execute(ProfileSubscriber())
    }

    fun selectLanguage(languageCode: String) {
        selectLanguageUseCase.execute(SelectSettingSubscriber(),
            SelectLanguageUseCase.Params(languageCode))
    }

    fun selectStreamingServer(serverTag: String) {
        selectServerUseCase.execute(SelectSettingSubscriber(),
            SelectStreamingServerUseCase.Params(serverTag))
    }

    fun selectCacheSize(newCacheSize: Long) {
        selectCacheSizeUseCase.execute(SelectSettingSubscriber(),
            SelectCacheSizeUseCase.Params(newCacheSize))
    }

    fun selectStreamBitrate(newBitrate: Int) {
        selectStreamBitrateUseCase.execute(SelectSettingSubscriber(),
            SelectStreamBitrateUseCase.Params(newBitrate))
    }


    fun selectDeviceModel(newDeviceModelId: Long) {
        selectDeviceModelUseCase.execute(SelectSettingSubscriber(),
            SelectDeviceModelUseCase.Params(newDeviceModelId))
    }

    fun dispose() {
// FIXME Disposing here removes and not restores the behavior subjects subscriptions. Subscribing "onStart" does not help either.
//
//        valuesUseCase.dispose()
//        profileUseCase.dispose()
//        selectServerUseCase.dispose()
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
        override fun onComplete() {
            eventChannel.postValue(Event(LANGUAGE_CHANGED))
        }
        override fun onError(e: Throwable) = liveSettingValues.postValue(Resource.error(e))
    }
}