package org.alsi.android.domain.vod.repository

import io.reactivex.Observable
import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.session.VodBrowseCursor
import org.alsi.android.domain.vod.model.session.VodBrowseCursorReference
import org.alsi.android.domain.vod.model.session.VodBrowsePage

abstract class VodBrowseCursorRepository {

    /** Current cursor value available immediately but stored asynchronously.
     */
    protected var cursor = VodBrowseCursor()

    /** Set cursor immediately, store asynchronously.
     *
     * Skipped parameters "inherited" from previous cursor value
     */

    fun moveCursorTo(
        section: VodSection? = null,
        unit: VodUnit? = null,
        item: VodListingItem? = null,
        itemPosition: Int? = null,
        page: VodBrowsePage? = null,
        reuse: Boolean = false

    ) : Single<VodBrowseCursor> {

        val previousCursor = cursor
        cursor = VodBrowseCursor(
                section = if (reuse && null == section) previousCursor.section else section,
                unit = if (reuse && null == unit) previousCursor.unit else unit,
                item = if (reuse && null == item) previousCursor.item else item,
                itemPosition = if (reuse && null == itemPosition) previousCursor.itemPosition else itemPosition,
                page = page)
        return finalizeCursorSetting(previousCursor)
    }

    fun resetCursor() {
        cursor = VodBrowseCursor()
    }

    /** Store current cursor value. It is reasonable to store a few last cursor values to
     * have playback history.
     */
    protected abstract fun finalizeCursorSetting(previousCursor: VodBrowseCursor?)
            : Single<VodBrowseCursor>

    /** Get current cursor
     */
    abstract fun getCursor(): Single<VodBrowseCursor>

    /** Get locally stored cursor reference to restore it (in order to continue from the
     * last browsing point)
     */
    abstract fun getStoredCursorReference(): Single<VodBrowseCursorReference?>

    /** Get cursor observable
     */
    abstract fun observeCursor(): Observable<VodBrowseCursor>
    abstract fun mostRecent(serviceId: Long): Single<UserActivityRecord?>
}