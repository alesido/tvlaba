package org.alsi.android.domain.streaming.repository

import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord

abstract class SessionRepository {

    /** Parental control password basically does not outlives service session. More precisely,
     *  it's dismissed when user leaves channels which are under parent control
     */
    var parentalControlPassword: String? = null

    /** ... to restore previous session context
     */
    abstract fun mostRecentActivity(serviceId: Long): Single<UserActivityRecord?>
}