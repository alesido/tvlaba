package org.alsi.android.domain.user.model

import org.joda.time.LocalDate

enum class SubscriptionStatus {
    UNKNOWN, USER_NOT_SUBSCRIBED, ACTIVE, EXPIRED
}

/**
 * Service subscription fully identified by service ID
 */
class ServiceSubscription(
        val serviceId: Long,
        val status: SubscriptionStatus,
        val expirationDate: LocalDate?
)
