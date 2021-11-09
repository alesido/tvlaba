package org.alsi.android.local.store.settings

import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.SubscriptionPackage
import org.alsi.android.domain.user.model.SubscriptionStatus
import org.alsi.android.domain.user.model.UserAccount
import org.joda.time.LocalDate

object AccountTestDataFactory {

    fun account(): UserAccount {
        val subscriptions: MutableList<ServiceSubscription> = mutableListOf()

        val packageTitle = "Movies, Science"
        subscriptions.add(ServiceSubscription(
            1L, SubscriptionPackage(
                id = packageTitle.hashCode().toLong(),
                title = packageTitle,
                packets = packageTitle.split(",").map { it.trim() }
            ),
            expirationDate = LocalDate.now().plusMonths(1),
            status = SubscriptionStatus.ACTIVE
        ))


        val packageTitle2 = "Test VOD Premium"
        subscriptions.add(ServiceSubscription(
            2L, SubscriptionPackage(
                id = packageTitle2.hashCode().toLong(),
                title = packageTitle2,
                packets = listOf("Premium")
            ),
            expirationDate = LocalDate.now().plusMonths(3),
            status = SubscriptionStatus.ACTIVE
        ))

        return UserAccount(
            "TestUserAccount",
            "TestUserPassword",
            subscriptions
        )
    }

    fun accountUpdate(): UserAccount {
        val subscriptions: MutableList<ServiceSubscription> = mutableListOf()

        val packageTitle = "Movies, Science, Entertainment"
        subscriptions.add(ServiceSubscription(
            1L, SubscriptionPackage(
                id = packageTitle.hashCode().toLong(),
                title = packageTitle,
                packets = packageTitle.split(",").map { it.trim() }
            ),
            expirationDate = LocalDate.now().plusMonths(3),
            status = SubscriptionStatus.ACTIVE
        ))


        val packageTitle2 = "Test VOD Premium"
        subscriptions.add(ServiceSubscription(
            2L, SubscriptionPackage(
                id = packageTitle2.hashCode().toLong(),
                title = packageTitle2,
                packets = listOf("Premium")
            ),
            expirationDate = LocalDate.now().plusMonths(1),
            status = SubscriptionStatus.EXPIRED
        ))

        return UserAccount(
            "TestUserAccount",
            "TestUserPassword",
            subscriptions
        )
    }
}