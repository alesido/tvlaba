package org.alsi.android.local.model.user

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import io.objectbox.relation.ToMany

/**
 * Created on 7/26/18.
 */
@Entity
data class UserAccountEntity (

        @Id(assignable = true) var id: Long,

        @Unique
        var loginName: String,

        var loginPassword: String) {

    @Backlink
    lateinit var subscriptions: ToMany<SubscriptionEntity>
}