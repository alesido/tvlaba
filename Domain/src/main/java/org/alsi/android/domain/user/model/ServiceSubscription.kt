package org.alsi.android.domain.user.model

import org.joda.time.LocalDate
import org.alsi.android.domain.streaming.model.StreamingServiceKind

enum class SubscriptionStatus {
    UNKNOWN, USER_NOT_SUBSCRIBED, ACTIVE, EXPIRED
}

class ServiceSubscription(
        val id: Long,
        val serviceId: Long,
        val status: SubscriptionStatus,
        val expirationDate: LocalDate?
)
