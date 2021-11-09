package org.alsi.android.moidom.mapper

import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceRegistry
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.SubscriptionPackage
import org.alsi.android.domain.user.model.SubscriptionStatus
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.moidom.Moidom
import org.alsi.android.moidom.model.LoginResponse
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.remote.mapper.SourceDataMapper
import org.joda.time.LocalDate

/**
 * TODO Decide what to do in case service configured for user, but not implemented in the app.
 */
class AccountSourceDataMapperMoiDom (

    private val defaultTvServiceId: Long,
    private val defaultVodServiceId: Long,

    private val loginName: String,
    private val loginPassword: String,

    private val registry: StreamingServiceRegistry
)
    : SourceDataMapper<LoginResponse, UserAccount>
{

    override fun mapFromSource(source: LoginResponse): UserAccount {
        // subscriptions
        val subscriptions: MutableList<ServiceSubscription> = mutableListOf()

        // -- default TV subscription package, single here for all services (?)
        val tvPacketsString = source.account.packet_name
        val tvPacketsList = tvPacketsString.split(",").map { it.trim() }
        val tvSubscriptionPackage = SubscriptionPackage(
            id = tvPacketsString.hashCode().toLong(),
            title = tvPacketsString,
            packets = tvPacketsList,
        )
        val tvSubscriptionExpiration = LocalDate(source.account.packet_expire.toLong() * 1000L)

        // - default TV service subscription
        subscriptions.add(ServiceSubscription(
            serviceId = defaultTvServiceId,
            subscriptionPackage = tvSubscriptionPackage,
            status = if (tvSubscriptionExpiration.isAfter(LocalDate.now()))
                SubscriptionStatus.ACTIVE else SubscriptionStatus.USER_NOT_SUBSCRIBED,
            expirationDate = tvSubscriptionExpiration
        ))

        // other subscriptions
        source.services.entries.forEach {
            mapServiceTag(it.key)?.let {_ ->
                val service = registry.serviceByTag[it.key]
                if (service != null) {
                    subscriptions.add(ServiceSubscription(
                        serviceId = service.id,
                        subscriptionPackage = tvSubscriptionPackage,
                        status = if (it.value == 1) SubscriptionStatus.ACTIVE
                            else SubscriptionStatus.USER_NOT_SUBSCRIBED,
                        expirationDate = tvSubscriptionExpiration
                    ))
                }
            }
        }

        return UserAccount(loginName, loginPassword, subscriptions)
    }

    private fun mapServiceTag(sourceTag: String): String? = when(sourceTag) {
            RestServiceMoidom.TAG_SERVICE_MOIDOM_VOD -> "${Moidom.TAG}.${StreamingService.VOD}"
            RestServiceMoidom.TAG_SERVICE_MEGOGO_VOD -> "megogo.${StreamingService.VOD}"
            else -> null
        }
}