package org.alsi.android.domain.context.model

import org.alsi.android.domain.context.model.SessionActivityType.*

class UserActivityRecord (
    val loginName: String,
    val serviceId: Long,
    val activityType: SessionActivityType,
    val timeStamp: Long
) {
    fun isEmpty() = loginName == "unknown" && serviceId == -1L && activityType == NONE && timeStamp == -1L

    companion object {
        fun empty() = UserActivityRecord("unknown", -1L, NONE, -1L)

        // ... pseudo activity just to initiate login
        fun requireLogin() = UserActivityRecord("unknown", -1L, LOGIN, System.currentTimeMillis())
    }
}