package org.alsi.android.domain.streaming.repository

import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord

abstract class SessionRepository {
    abstract fun mostRecent(serviceId: Long): Single<UserActivityRecord?>
}