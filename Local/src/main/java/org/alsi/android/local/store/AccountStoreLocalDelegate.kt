package org.alsi.android.local.store

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.data.repository.account.AccountDataLocal
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.local.Local
import org.alsi.android.local.mapper.AccountEntityMapper
import org.alsi.android.local.mapper.SubscriptionEntityMapper
import org.alsi.android.local.model.user.UserAccountEntity
import javax.inject.Inject
import javax.inject.Named

/**
 *  Assumption. It's supposed that account IDs are unique across the services
 */
class AccountStoreLocalDelegate @Inject constructor(
        @Named(Local.STORE_NAME) val boxStore: BoxStore
): AccountDataLocal
{
    private var accountId: Long? = null

    private val box: Box<UserAccountEntity> = boxStore.boxFor()

    private val accountMapper = AccountEntityMapper()
    private val subscriptionMapper = SubscriptionEntityMapper()

    /** TODO Test if non-existing account added while existing is updated if the source entity has ID = 0L
     */
    fun attach(account: UserAccount) {
        accountId = box.put(accountMapper.mapToEntity(account))
    }

    override fun addAttachAccount(account: UserAccount) {
        accountId = box.put(accountMapper.mapToEntity(account))
    }

    override fun getLoginName(): Single<String> {
        return Single.just(box.get(accountId?: return idUnknown()).loginName)
    }

    override fun getPassword(): Single<String> {
        return Single.just(box.get(accountId?: return idUnknown()).loginPassword)
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

    private fun idUnknown2(): Completable = Completable.error(Throwable(MESSAGE_ACCOUNT_ID_UNKNOWN))

    private fun noAccount(): Completable = Completable.error(Throwable(MESSAGE_ACCOUNT_NOT_STORED))

    companion object {
        const val MESSAGE_ACCOUNT_ID_UNKNOWN = "Local account store delegate doesn't \"know\" it's account!"
        const val MESSAGE_ACCOUNT_NOT_STORED = "Attempt to update property value of a not stored yet account!"
    }
}