package org.alsi.android.domain.streaming.model

import io.reactivex.Single
import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.repository.SettingsRepository
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.domain.user.repository.AccountDataService

abstract class ServiceProvider(
        val id: Long,
        val name: String,
        val accountService: AccountDataService,
        val settingsRepository: SettingsRepository,
        private val services: List<StreamingService>) {

    fun serviceByTag(tag: String): StreamingService? {
        services.forEach{ if (it.tag == tag) return it }
        return null
    }

    abstract fun login(loginName: String, loginPassword: String): Single<UserAccount>
}