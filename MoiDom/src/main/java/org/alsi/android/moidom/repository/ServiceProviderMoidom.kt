package org.alsi.android.moidom.repository

import io.reactivex.Single
import org.alsi.android.domain.streaming.model.ServiceProvider
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.moidom.Moidom
import javax.inject.Inject
import javax.inject.Named

class ServiceProviderMoidom @Inject constructor(

        @Named(Moidom.TAG) id: Long,
        @Named(Moidom.TAG) name: String,
        accountService: AccountDataServiceMoidom,
        settingsRepository: SettingsRepositoryMoidom,
        @Named(Moidom.TAG) services: List<StreamingService>)

    : ServiceProvider(id, name, accountService, settingsRepository, services) {

    override fun login(loginName: String, loginPassword: String): Single<UserAccount> {
        return accountService.login(loginName, loginPassword)
    }
}