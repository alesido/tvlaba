package org.alsi.android.local.mapper

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.model.user.UserAccountEntity

class AccountEntityMapper: EntityMapper<UserAccountEntity, UserAccount> {

    private val subscriptionMapper = SubscriptionEntityMapper()

    override fun mapFromEntity(entity: UserAccountEntity): UserAccount {
        val subscriptions : MutableList<ServiceSubscription> = mutableListOf()
        for (subscriptionEntity in entity.subscriptions) {
            subscriptions.add(subscriptionMapper.mapFromEntity(subscriptionEntity))
        }
        return with(entity) {
            UserAccount(loginName, loginPassword, subscriptions)
        }
    }

    override fun mapToEntity(domain: UserAccount): UserAccountEntity {
        val entity = with(domain) {
            UserAccountEntity(0L, loginName, loginPassword)
        }
        for (subscription in domain.subscriptions) {
            entity.subscriptions.add(subscriptionMapper.mapToEntity(subscription))
        }
        return entity
    }
}