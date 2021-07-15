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
import org.alsi.android.local.model.user.UserAccountEntity
import org.alsi.android.local.model.user.UserAccountEntity_
import org.alsi.android.local.model.user.UserAccountSubject

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

    private val accountMapper = AccountEntityMapper()
    private val subscriptionMapper = SubscriptionEntityMapper()


    override fun addAttachAccount(account: UserAccount) {
        val entity = accountMapper.mapToEntity(account)
        val record = box.query {equal(UserAccountEntity_.loginName, account.loginName)}.findFirst()
        record?.let{ entity.id = it.id }
        accountId = box.put(entity)
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

    override fun setSubscriptions(subscriptions: List<ServiceSubscription>): Completable {
        return Completable.fromRunnable {
            accountId?.let {  id -> box.get(id)?.let { account ->
                account.subscriptions.clear()
                subscriptions.forEach { account.subscriptions.add( subscriptionMapper.mapToEntity(it)) }
                box.put(account)
            } ?: noAccount()
            } ?: idUnknown2()
        }
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