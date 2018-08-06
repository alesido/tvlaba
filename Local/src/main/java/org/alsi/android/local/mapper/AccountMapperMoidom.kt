package org.alsi.android.local.mapper

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.model.user.UserAccountEntity

class AccountMapperMoidom: EntityMapper<UserAccountEntity, UserAccount> {

    private val subscriptionMapper = SubscriptionMapperMoidom()

    override fun mapFromEntity(entity: UserAccountEntity): UserAccount {
        val subscriptions : MutableList<ServiceSubscription> = mutableListOf()
        for (subscriptionEntity in entity.subscriptions) {
            subscriptions.add(subscriptionMapper.mapFromEntity(subscriptionEntity))
        }
        return with(entity) {
            UserAccount(id, loginName, loginPassword, parentCode, languageCode, subscriptions)
        }
    }

    override fun mapToEntity(domain: UserAccount): UserAccountEntity {
        val entity = with(domain) {
            UserAccountEntity(id, loginName, loginPassword, languageCode)
        }
        for (subscription in domain.subscriptions) {
            entity.subscriptions.add(subscriptionMapper.mapToEntity(subscription))
        }
        return entity
    }
}