package org.alsi.android.domain.vod.model.session

import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.streaming.repository.SessionRepository
import org.alsi.android.domain.vod.repository.VodBrowseCursorRepository
import org.alsi.android.domain.vod.repository.VodPlayCursorRepository

open class VodSessionRepository (
        val browse: VodBrowseCursorRepository,
        val play: VodPlayCursorRepository

): SessionRepository() {

        override fun mostRecentActivity(serviceId: Long): Single<UserActivityRecord?> {
                return Single.zip(browse.mostRecent(serviceId), play.mostRecent(serviceId), {
                                browsing, playing -> Pair(browsing, playing)
                }).flatMap {
                        val (browsing, playing) = it
                        if (browsing.isEmpty() && playing.isEmpty() || playing.isEmpty())
                                return@flatMap Single.just(browsing)
                        val mostRecent = if (browsing.timeStamp > playing.timeStamp)
                                browsing else playing
                        return@flatMap Single.just(mostRecent)
                }
        }
}