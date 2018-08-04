package org.alsi.android.moidom.store.internal.mapper

import org.alsi.android.domain.streaming.model.StreamingService
import org.alsi.android.domain.streaming.model.StreamingServiceRegistry
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.store.remote.RestServiceMoidom
import org.alsi.android.moidom.model.local.user.StatusProperty
import org.alsi.android.moidom.model.local.user.SubscriptionEntity
import org.alsi.android.moidom.model.local.user.UserAccountEntity
import org.alsi.android.moidom.model.remote.LoginResponse
import org.alsi.android.remote.mapper.SourceDataMapper
import javax.inject.Inject

/**
 * TODO Decide what to do in case service configured for user, but not implemented in the app.
 *
 */
class AccountSourceDataMapper @Inject constructor(

        private val loginName: String,
        private val loginPassword: String)

    : SourceDataMapper<LoginResponse, UserAccountEntity> {

    @Inject lateinit var registry: StreamingServiceRegistry

    override fun mapFromSource(source: LoginResponse): UserAccountEntity {
        val entity = UserAccountEntity(0L, loginName, loginPassword, source.settings.language.value)
        source.services.entries.forEach {
            val tag = when(it.key) {
                RestServiceMoidom.TAG_SERVICE_MOIDOM_TV -> "${Moidom.TAG}.${StreamingService.TV}"
                RestServiceMoidom.TAG_SERVICE_MOIDOM_VOD -> "${Moidom.TAG}.${StreamingService.VOD}"
                RestServiceMoidom.TAG_SERVICE_MEGOGO_VOD -> "megogo.${StreamingService.VOD}"
                else -> null
            }
            if (tag != null) {
                val service = registry.serviceByTag[it.key]
                if (service != null) {
                    entity.subscriptions.add(SubscriptionEntity(0L, service.id,
                            if (it.value == 1) StatusProperty.ACTIVE else StatusProperty.USER_NOT_SUBSCRIBED,
                            null))
                }
            }
        }
        return entity
    }
}