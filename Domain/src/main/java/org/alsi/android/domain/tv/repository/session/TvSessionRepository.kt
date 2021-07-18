package org.alsi.android.domain.tv.repository.session

import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.streaming.repository.SessionRepository

class TvSessionRepository (
        val browse: TvBrowseCursorRepository,
        val play: TvPlayCursorRepository

): SessionRepository() {

        override fun mostRecent(serviceId: Long): Single<UserActivityRecord?> {
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