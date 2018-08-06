package org.alsi.android.local.store

import io.objectbox.Box
import io.reactivex.Completable
import io.reactivex.Single
import io.objectbox.BoxStore
import org.alsi.android.data.repository.account.AccountDataLocal
import org.alsi.android.domain.user.model.ServiceSubscription
import org.alsi.android.local.Local
import org.alsi.android.local.model.user.UserAccountEntity
import org.alsi.android.local.model.user.UserAccountEntity_
import javax.inject.Inject
import javax.inject.Named

/**
 * Created on 7/26/18.
 */
class AccountStoreLocalDelegate @Inject constructor(
        @Named(Local.STORE_NAME) private val boxStore: BoxStore)
    : AccountDataLocal
{
    private val box: Box<UserAccountEntity> = boxStore.boxFor(UserAccountEntity::class.java)

    var subjectId: Long? = null

    fun attach(subject: UserAccountEntity) {
        subjectId = box.query().equal(UserAccountEntity_.loginName, subject.loginName).build().findUnique()?.id?: 0L
        subjectId = box.put(subject)
    }

    override fun getLoginName(): Single<String> {
        return Single.just(box.get(subjectId?: return Single.error(Throwable(""))).loginName)
    }

    override fun getPassword(): Single<String> {
        return Single.just(box.get(subjectId?: return Single.error(Throwable(""))).loginPassword)
    }

    override fun getParentCode(): Single<String> {
        return Single.just(box.get(subjectId?: return Single.error(Throwable(""))).parentCode)
    }

    override fun getLanguage(): Single<String> {
        return Single.just(box.get(subjectId?: return Single.error(Throwable(""))).languageCode)
    }

    override fun getTimeShiftSettingHours(): Single<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSubscriptions(): Single<List<ServiceSubscription>> {
        TODO("Add mapper for subscription entity to domain object") //To change body of created functions use File | Settings | File Templates.
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