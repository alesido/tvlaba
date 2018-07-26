package org.alsi.android.domain.user.model

/**
 * Created on 7/15/18.
 */
class UserAccount(
        val loginName: String,
        val loginPassword: String,
        val parentCode: String,
        val languageCode: String,
        val subscriptions: List<ServiceSubscription>
)
