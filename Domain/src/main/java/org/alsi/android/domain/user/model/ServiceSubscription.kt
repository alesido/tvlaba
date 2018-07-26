package org.alsi.android.domain.user.model

import org.joda.time.LocalDate

enum class StreamingServiceType {
    TV_LIVE, TV_ARCHIVE, VOD, RADIO
}

class ServiceSubscription(val id: String, val type: StreamingServiceType, val providerId: String, val expirationDate: LocalDate)
