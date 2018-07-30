package org.alsi.android.moidom.store.internal.mapper

import org.alsi.android.domain.streaming.model.ServiceProvider
import org.alsi.android.moidom.model.local.user.StatusProperty
import org.alsi.android.moidom.model.local.user.SubscriptionEntity
import org.alsi.android.moidom.model.local.user.UserAccountEntity
import org.alsi.android.moidom.model.remote.LoginResponse
import org.alsi.android.remote.mapper.SourceDataMapper

/**
 * TODO Decide what to do in case service configured for user, but not implemented in the app.
 */
class AccountSourceDataMapper(
        private val loginName: String,
        private val loginPassword: String,
        private val provider: ServiceProvider)
    : SourceDataMapper<LoginResponse, UserAccountEntity>
{
    override fun mapFromSource(source: LoginResponse): UserAccountEntity {
        val entity = UserAccountEntity(0L, loginName, loginPassword, source.settings.language.value)
        source.services.entries.forEach {
            val service = provider.serviceByTag(it.key)
            if (service != null) {
                entity.subscriptions.add(SubscriptionEntity(0L, service.id,
                        if (it.value == 1) StatusProperty.ACTIVE else StatusProperty.USER_NOT_SUBSCRIBED,
                        null))
            }
        }
        return entity
    }
}