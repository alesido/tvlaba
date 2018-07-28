package org.alsi.android.moidom.store

import io.reactivex.Completable
import io.reactivex.Single
import io.objectbox.BoxStore
import org.alsi.android.data.account.AccountDataLocal
import org.alsi.android.domain.user.model.ServiceSubscription
import javax.inject.Inject

/**
 * Created on 7/26/18.
 */
class AccountStoreLocaMoidom @Inject constructor(private val boxStore: BoxStore) : AccountDataLocal
{
    override fun getLoginName(): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPassword(): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getParentCode(): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLanguage(): Single<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTimeShiftSettingHours(): Single<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSubscriptions(): Single<List<ServiceSubscription>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setParentCode(code: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLanguage(currentCode: String): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTimeShiftSettingHours(hours: Int): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setSubscriptions(subscription: List<ServiceSubscription>): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}