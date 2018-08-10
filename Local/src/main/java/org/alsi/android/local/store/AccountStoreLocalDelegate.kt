package org.alsi.android.local.store

import io.objectbox.Box
import io.reactivex.Completable
import io.reactivex.Single
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import org.alsi.android.data.repository.account.AccountDataLocal
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.local.Local
import org.alsi.android.local.mapper.SubscriptionEntityMapper
import org.alsi.android.local.model.user.UserAccountEntity
import org.alsi.android.local.model.user.UserAccountEntity_
import javax.inject.Inject
import javax.inject.Named

/**
 *  Assumption. It's supposed that account IDs are unique across the services
 */
class AccountStoreLocalDelegate @Inject constructor(
        @Named(Local.STORE_NAME) private val boxStore: BoxStore)
    : AccountDataLocal
{
    private var accountId: Long? = null

    private val box: Box<UserAccountEntity> = boxStore.boxFor()

    private val subscriptionMapper = SubscriptionEntityMapper()

    fun attach(account: UserAccountEntity) {
        accountId = box.query().equal(UserAccountEntity_.loginName, account.loginName).build().findUnique()?.id?: 0L
        accountId = box.put(account)
    }

    override fun getLoginName(): Single<String> {
        return Single.just(box.get(accountId?: return idUnknown()).loginName)
    }

    override fun getPassword(): Single<String> {
        return Single.just(box.get(accountId?: return idUnknown()).loginPassword)
    }

    override fun getParentCode(): Single<String> {
        return Single.just(box.get(accountId?: return idUnknown()).parentCode)
    }

    override fun getLanguage(): Single<String> {
        return Single.just(box.get(accountId?: return idUnknown()).languageCode)
    }

    override fun getTimeShiftSettingHours(): Single<Int> {
        return Single.just(box.get(accountId?: return idUnknown()).timeShiftSettingHours)
    }

    override fun getSubscriptions(): Single<List<ServiceSubscription>> {
        return Single.just( box.get(accountId?: return idUnknown())
                .subscriptions.toList().map { subscriptionMapper.mapFromEntity(it) })
    }

    override fun setParentCode(code: String): Completable {
        return Completable.fromRunnable {
            accountId?.let {  id -> box.get(id)?.let { it.parentCode = code; box.put(it)
            } ?: noAccount()
            } ?: idUnknown2()
        }
    }

    override fun setLanguage(code: String): Completable {
        return Completable.fromRunnable {
            accountId?.let {  id -> box.get(id)?.let { it.languageCode = code; box.put(it)
            } ?: noAccount()
            } ?: idUnknown2()
        }
    }

    override fun setTimeShiftSettingHours(hours: Int): Completable {
        return Completable.fromRunnable {
            accountId?.let {  id -> box.get(id)?.let { it.timeShiftSettingHours = hours; box.put(it)
            } ?: noAccount()
            } ?: idUnknown2()
        }
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