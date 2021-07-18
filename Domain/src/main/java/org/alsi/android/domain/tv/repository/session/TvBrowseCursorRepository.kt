package org.alsi.android.domain.tv.repository.session

import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.domain.tv.model.guide.TvDaySchedule
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvBrowsePage

abstract class TvBrowseCursorRepository {

    /** Current cursor value available immediately but stored asynchronously.
     */
    protected var cursor = TvBrowseCursor()

    /** Set cursor immediately, store synchronously.
     *
     * Skipped parameters "inherited" from previous cursor value
     */

    fun moveCursorTo(
            category: TvChannelCategory? = null,
            channel: TvChannel? = null,
            schedule: TvDaySchedule? = null,
            program: TvProgramIssue? = null,
            page: TvBrowsePage? = null,
            reuse: Boolean = false

    ) : Single<TvBrowseCursor> {

        val previousCursor = cursor
        cursor = TvBrowseCursor(
                category = if (reuse && null == category) previousCursor.category else category,
                channel = if (reuse && null == channel) previousCursor.channel else channel,
                schedule = if (reuse && null == schedule) previousCursor.schedule else schedule,
                program = if (reuse && null == program) previousCursor.program else program,
                page = page)
        return finalizeCursorSetting(previousCursor)
    }

    fun resetCursor() {
        cursor = TvBrowseCursor()
    }

    /** Store current cursor value. It is reasonable to store a few last cursor values to
     * have playback history.
     */
    protected abstract fun finalizeCursorSetting(previousCursor: TvBrowseCursor?)
            : Single<TvBrowseCursor>

    /** Get current cursor
     */
    abstract fun getCursor(): Single<TvBrowseCursor>

    /** Get cursor observable
     */
    abstract fun observeCursor(): Observable<TvBrowseCursor>
    abstract fun mostRecent(): Single<UserActivityRecord?>
}