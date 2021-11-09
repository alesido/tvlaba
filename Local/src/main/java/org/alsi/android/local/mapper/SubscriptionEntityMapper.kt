package org.alsi.android.local.mapper

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.SubscriptionPackage
import org.alsi.android.domain.user.model.SubscriptionStatus
import org.alsi.android.domain.user.model.SubscriptionStatus.UNKNOWN
import org.alsi.android.local.model.user.StatusProperty
import org.alsi.android.local.model.user.SubscriptionEntity
import org.alsi.android.local.model.user.SubscriptionPackageEntity

class SubscriptionEntityMapper: EntityMapper<SubscriptionEntity, ServiceSubscription> {

    override fun mapFromEntity(entity: SubscriptionEntity): ServiceSubscription {
        return with(entity) {
            ServiceSubscription(serviceId?: 0L,
                subscriptionPackage.target?.let {
                    SubscriptionPackage(it.id, it.title, it.termMonths, it.packets)
                }?: SubscriptionPackage(-1L),
                status?.reference?: UNKNOWN, expirationDate)
        }
    }

    override fun mapToEntity(domain: ServiceSubscription): SubscriptionEntity {
        return with (domain) {
            val entity = SubscriptionEntity( 0L, serviceId,
                    StatusProperty.valueByReference[status]?: StatusProperty.UNKNOWN,
                    expirationDate)
            entity.subscriptionPackage.target = with(subscriptionPackage) {
                SubscriptionPackageEntity(id, title, termMonths, packets)
            }
            entity
        }
    }
}