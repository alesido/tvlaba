package org.alsi.android.moidom.repository

import io.reactivex.subjects.PublishSubject
import org.alsi.android.data.repository.settings.SettingsDataGateway
import org.alsi.android.local.model.settings.ServiceSettingsEntity
import org.alsi.android.local.store.settings.SettingsStoreLocalDelegate
import org.alsi.android.moidom.model.LoginEvent
import org.alsi.android.moidom.store.settings.SettingsRemoteStoreMoidom
import javax.inject.Inject

class SettingsRepositoryMoidom (providerId: Long): SettingsDataGateway(
        SettingsRemoteStoreMoidom(),
        SettingsStoreLocalDelegate(ServiceSettingsEntity.SCOPE_PROVIDER, providerId)) {

    @Inject lateinit var loginSubject: PublishSubject<LoginEvent>

    init {
        loginSubject.subscribe {

            val localMoidom = local as SettingsStoreLocalDelegate
            val remoteMoidom = remote as SettingsRemoteStoreMoidom

            localMoidom.attach(it.account)

            val profile = remoteMoidom.getSourceProfile(it.data)

            localMoidom.setProfile(profile)
            localMoidom.setValues(remoteMoidom.getSourceSettings(it.data, profile))
        }
    }
}
