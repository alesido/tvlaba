package org.alsi.android.local.model.user

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne
import org.alsi.android.domain.user.model.UserAccount

/**
 * Created on 7/26/18.
 */
@Entity
data class UserAccountEntity (

        @Id(assignable = true) var id: Long? = 0L,

        @Unique
        var loginName: String? = UserAccount.GUEST_LOGIN,

        var loginPassword: String? = UserAccount.GUEST_PASS
) {
    @Backlink
    lateinit var subscriptions: ToMany<SubscriptionEntity>

    lateinit var preferences: ToOne<UserPreferencesEntity>
}