package org.alsi.android.moidom.repository.vod

import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.streaming.repository.SessionRepository

class VodSessionRepositoryMoidom: SessionRepository() {
    override fun mostRecentActivity(serviceId: Long): Single<UserActivityRecord?> {
        // stub!
        return Single.just(UserActivityRecord.empty())
    }
}