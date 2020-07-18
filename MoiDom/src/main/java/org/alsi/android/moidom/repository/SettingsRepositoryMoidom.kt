package org.alsi.android.moidom.repository

import android.util.Log
import io.objectbox.BoxStore
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.alsi.android.data.repository.settings.SettingsDataGateway
import org.alsi.android.domain.streaming.model.service.StreamingServiceDefaults
import org.alsi.android.local.Local
import org.alsi.android.local.model.settings.ServiceSettingsEntity
import org.alsi.android.local.store.settings.SettingsStoreLocalDelegate
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.model.LoginEvent
import org.alsi.android.moidom.store.settings.SettingsRemoteStoreMoidom
import javax.inject.Inject
import javax.inject.Named

class SettingsRepositoryMoidom @Inject constructor(

        @Named(Moidom.TAG) providerId: Long,
        @Named(Local.STORE_NAME) localStore: BoxStore,
        defaults: StreamingServiceDefaults,
        loginSubject: PublishSubject<LoginEvent>
)
    : SettingsDataGateway(
        SettingsRemoteStoreMoidom(),
        SettingsStoreLocalDelegate(
                ServiceSettingsEntity.SCOPE_PROVIDER,
                providerId, localStore, defaults)) {

    private var subscription: Disposable

    init {
        subscription = loginSubject.subscribe( {

            val localMoidom = local as SettingsStoreLocalDelegate
            val remoteMoidom = remote as SettingsRemoteStoreMoidom

            localMoidom.attach(it.account)

            val profile = remoteMoidom.getSourceProfile(it.data)

            localMoidom.setProfile(profile)
            localMoidom.setValues(remoteMoidom.getSourceSettings(it.data, profile))

        }, {
            Log.e(SettingsRepositoryMoidom::class.simpleName, "Exception Ignored: $it")
        })
    }

    fun dispose() {
        if (!subscription.isDisposed) subscription.dispose()
    }
}
