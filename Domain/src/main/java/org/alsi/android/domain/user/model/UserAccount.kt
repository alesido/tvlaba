package org.alsi.android.domain.user.model

/**
 * Created on 7/15/18.
 */
class UserAccount(
        val id : Long,
        val loginName: String,
        val loginPassword: String,
        val parentCode: String,
        val languageCode: String,
        val timeShiftSettingHours: Int,
        val subscriptions: List<ServiceSubscription>
)
