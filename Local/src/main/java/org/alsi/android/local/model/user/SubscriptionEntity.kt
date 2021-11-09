package org.alsi.android.local.model.user

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Convert
import io.objectbox.converter.PropertyConverter
import io.objectbox.relation.ToOne
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.SubscriptionStatus
import org.alsi.android.local.framework.objectbox.LocalDateConverter
import org.joda.time.LocalDate

@Entity
data class SubscriptionEntity (

        @Id var id: Long? = 0,

        var serviceId : Long? = 0,

        @Convert(converter = StatusPropertyConverter::class, dbType = Long::class)
        var status: StatusProperty? = null,

        @Convert(converter = LocalDateConverter::class, dbType = Long::class)
        var expirationDate: LocalDate? = null
) {
    fun updateWith(source: ServiceSubscription) {
        serviceId = source.serviceId
        status = StatusProperty.valueByReference[source.status]?: StatusProperty.UNKNOWN
        expirationDate = source.expirationDate
    }

    lateinit var userAccount : ToOne<UserAccountEntity>
    lateinit var subscriptionPackage: ToOne<SubscriptionPackageEntity>
}

enum class StatusProperty(val id: Long, val reference: SubscriptionStatus) {
    UNKNOWN(0L, SubscriptionStatus.UNKNOWN),
    USER_NOT_SUBSCRIBED(1L, SubscriptionStatus.USER_NOT_SUBSCRIBED),
    ACTIVE(2L, SubscriptionStatus.ACTIVE),
    EXPIRED(3L, SubscriptionStatus.EXPIRED);
    companion object {
        val valueById = StatusProperty.values().map { it.id to it }.toMap()
        val valueByReference = StatusProperty.values().map { it.reference to it }.toMap()
    }
}

class StatusPropertyConverter : PropertyConverter<StatusProperty, Long> {
    override fun convertToDatabaseValue(entityProperty: StatusProperty?): Long {
        return entityProperty?.id?: StatusProperty.UNKNOWN.id
    }

    override fun convertToEntityProperty(databaseValue: Long?): StatusProperty {
        return databaseValue?. let { StatusProperty.valueById[it]}?: StatusProperty.UNKNOWN
    }
}