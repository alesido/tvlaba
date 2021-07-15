package org.alsi.android.domain.user.model

/**
 * Created on 7/15/18.
 */
class UserAccount(
        val loginName: String,
        val loginPassword: String,
        val subscriptions: List<ServiceSubscription>
) {
        fun isGuest() = loginName == "guest"

        companion object {
                fun guest() = UserAccount("guest", "", listOf())
        }
}
