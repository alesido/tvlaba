package org.alsi.android.moidom.mapper

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.moidom.model.local.user.StatusProperty
import org.alsi.android.moidom.model.local.user.SubscriptionEntity

class SubscriptionMapperMoidom: EntityMapper<SubscriptionEntity, ServiceSubscription> {

    override fun mapFromEntity(entity: SubscriptionEntity): ServiceSubscription {
        return with(entity) {
            ServiceSubscription(id, serviceId, status.reference, expirationDate)
        }
    }

    override fun mapToEntity(domain: ServiceSubscription): SubscriptionEntity {
        return with (domain) {
            SubscriptionEntity(
                    id, serviceId,
                    StatusProperty.valueByReference[status]?: StatusProperty.UNKNOWN,
                    expirationDate)
        }
    }

}