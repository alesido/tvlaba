package org.alsi.android.moidom.mapper

import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceRegistry
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.SubscriptionStatus
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.moidom.model.LoginResponse
import org.alsi.android.remote.mapper.SourceDataMapper
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * TODO Decide what to do in case service configured for user, but not implemented in the app.
 */
class AccountSourceDataMapper @Inject constructor(

        private val loginName: String,
        private val loginPassword: String,
        private val registry: StreamingServiceRegistry
)
    : SourceDataMapper<LoginResponse, UserAccount>
{

    override fun mapFromSource(source: LoginResponse): UserAccount {
        val subscriptions: MutableList<ServiceSubscription> = mutableListOf()
        source.services.entries.forEach {
            mapServiceTag(it.key)?.let {_ ->
                val service = registry.serviceByTag[it.key]
                if (service != null) {
                    subscriptions.add(ServiceSubscription(
                            service.id,
                            if (it.value == 1) SubscriptionStatus.ACTIVE else SubscriptionStatus.USER_NOT_SUBSCRIBED,
                            LocalDate(source.account.packet_expire)))
                }
            }
        }
        return UserAccount(loginName, loginPassword, subscriptions)
    }

    private fun mapServiceTag(sourceTag: String): String? = when(sourceTag) {
            RestServiceMoidom.TAG_SERVICE_MOIDOM_TV -> "${Moidom.TAG}.${StreamingService.TV}"
            RestServiceMoidom.TAG_SERVICE_MOIDOM_VOD -> "${Moidom.TAG}.${StreamingService.VOD}"
            RestServiceMoidom.TAG_SERVICE_MEGOGO_VOD -> "megogo.${StreamingService.VOD}"
            else -> null
        }
}