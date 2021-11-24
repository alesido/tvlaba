package org.alsi.android.domain.user.model

/**
 * Created on 7/15/18.
 */
class UserAccount(
        val loginName: String,
        val loginPassword: String,
        val subscriptions: List<ServiceSubscription>,
        val preferences: UserPreferences? = UserPreferences()
) {
        fun isGuest() = loginName == "guest"

        companion object {

                const val GUEST_LOGIN = "guest"
                const val GUEST_PASS = ""

                fun guest() = UserAccount(GUEST_LOGIN, GUEST_PASS, listOf())
        }
}


