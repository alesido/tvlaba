package org.alsi.android.local.store

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.data.repository.account.AccountDataLocal
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.mapper.AccountEntityMapper
import org.alsi.android.local.mapper.SubscriptionEntityMapper
import org.alsi.android.local.model.user.*

/**
 *  Assumption. It's supposed that account IDs are unique across the services
 */
class AccountStoreLocalDelegate (
    boxStore: BoxStore,
    private val accountSubject: UserAccountSubject
): AccountDataLocal
{
    private var accountId: Long? = null

    private val box: Box<UserAccountEntity> = boxStore.boxFor()
    private val subscriptionBox: Box<SubscriptionEntity> = boxStore.boxFor()
    private val subscriptionPackageBox: Box<SubscriptionPackageEntity> = boxStore.boxFor()

    private val accountMapper = AccountEntityMapper()
    private val subscriptionMapper = SubscriptionEntityMapper()


    override fun addAttachAccount(account: UserAccount) {
        box.query {equal(UserAccountEntity_.loginName, account.loginName)}.findFirst()?.let {
            accountId = it.id
            it.loginName = account.loginName
            it.loginPassword = account.loginPassword
            updateSubscriptions(it, account.subscriptions)
        }?: run {
            accountId = insertSubscriptions(account)
        }
        accountSubject.onNext(account)
    }

    override fun attachAccountFor(loginName: String): UserAccount  {
        val record = box.query { equal(UserAccountEntity_.loginName, loginName) }.findFirst()
        return record?.let {
            accountId = it.id
            val account = accountMapper.mapFromEntity(record)
            accountSubject.onNext(account)
            account
        } ?: UserAccount.guest()
    }

    override fun getLoginName(): Single<String> {
        return Single.just(box.get(accountId?: return idUnknown()).loginName)
    }

    override fun getPassword(): Single<String> {
        return Single.just(box.get(accountId?: return idUnknown()).loginPassword)
    }

    override fun getPassword(loginName: String): Single<String> {
        val record = box.query {equal(UserAccountEntity_.loginName, loginName)}.findFirst()
        return Single.just((record?: return idUnknown()).loginPassword)
    }

    override fun getSubscriptions(): Single<List<ServiceSubscription>> {
        return Single.just( box.get(accountId?: return idUnknown())
                .subscriptions.toList().map { subscriptionMapper.mapFromEntity(it) })
    }

    override fun putSubscriptions(subscriptions: List<ServiceSubscription>): Completable {
        return Completable.fromRunnable {
            accountId?.let {  id -> box.get(id)?.let { account ->
                updateSubscriptions(account, subscriptions)
            } ?: noAccount()
            } ?: idUnknown2()
        }
    }

    private fun insertSubscriptions(source: UserAccount): Long {
        val entity = accountMapper.mapToEntity(source)
        box.attach(entity)
        source.subscriptions.map { insertSubscription(it) }
        return box.put(entity)
    }

    private fun insertSubscription(source: ServiceSubscription) = with(source) {
        // create subscription entity
        val subscriptionEntity = SubscriptionEntity(
            0L, serviceId,
            StatusProperty.valueByReference[status] ?: StatusProperty.UNKNOWN,
            expirationDate
        )
        subscriptionBox.attach(subscriptionEntity)
        // create package entity
        val packageEntity =  with(subscriptionPackage) {
            SubscriptionPackageEntity(id, title, termMonths, packets)
        }
        subscriptionPackageBox.put(packageEntity)
        // put subscription entity
        subscriptionEntity.subscriptionPackage.target = packageEntity
        subscriptionBox.put(subscriptionEntity)
        // result
        subscriptionEntity
    }

    private fun updateSubscriptions(entity: UserAccountEntity,
                                    sourceSubscriptions: List<ServiceSubscription>): Long {
        // working maps
        // NOTE Assumption: there is only one subscription per service
        val sourceMap = sourceSubscriptions.associateBy { it.serviceId }
        val targetMap = entity.subscriptions.associateBy { it.serviceId }

        // update existing subscription records
        entity.subscriptions.filter { sourceMap[it.serviceId] != null }.map {
            subscriptionBox.attach(it)
            val sourceItem = sourceMap[it.serviceId]
            if (it.subscriptionPackage.targetId != sourceItem!!.subscriptionPackage.id) {
                // the package replaced with a new one
                val packageEntity = with(sourceItem.subscriptionPackage) {
                    SubscriptionPackageEntity(id, title, termMonths, packets)
                }
                subscriptionPackageBox.put(packageEntity)
                it.subscriptionPackage.target = packageEntity
            }
            else {
                // the package probably updated
                // NOTE This based on assumption that serves maintains package IDs correctly
                it.subscriptionPackage.target.updateWith(sourceItem.subscriptionPackage)
                subscriptionPackageBox.put(it.subscriptionPackage.target)
            }
            it.apply { updateWith(sourceItem)}
        }.let {
            subscriptionBox.put(it)
        }

        // remove absent subscriptions
        targetMap.filter { null == sourceMap[it.key] }.map { it.value }.let {
            entity.subscriptions.removeAll(it)
            // NOTE Subscription package entities are not removed here cause they might be linked
            // to other subscriptions while package:subscriptions (1:n) is not maintained (extra
            // complication). And also, there are too small number of packages to make sense
            // to optimize their storage
        }

        // add new subscriptions
        sourceSubscriptions.filter { null == targetMap[it.serviceId] }
            .map { insertSubscription(it) }
            .let { subscriptionBox.put(it)}

        return box.put(entity)
    }

    private fun <T> idUnknown(): Single<T> = Single.error(Throwable(MESSAGE_ACCOUNT_ID_UNKNOWN))

    private fun <T> loginNameNotFound(): Single<T> = Single.error(Throwable(MESSAGE_ACCOUNT_LOGIN_UNKNOWN))

    private fun idUnknown2(): Completable = Completable.error(Throwable(MESSAGE_ACCOUNT_ID_UNKNOWN))

    private fun noAccount(): Completable = Completable.error(Throwable(MESSAGE_ACCOUNT_NOT_STORED))

    companion object {
        const val MESSAGE_ACCOUNT_ID_UNKNOWN = "Local account store delegate doesn't \"know\" an account by given ID!"
        const val MESSAGE_ACCOUNT_LOGIN_UNKNOWN = "Local account store delegate doesn't \"know\" an account by given name!"
        const val MESSAGE_ACCOUNT_NOT_STORED = "Attempt to update property value of a not stored yet account!"
    }
}