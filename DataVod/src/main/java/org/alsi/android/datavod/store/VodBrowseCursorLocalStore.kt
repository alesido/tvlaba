package org.alsi.android.datavod.store

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.vod.model.session.VodBrowseCursor
import org.alsi.android.domain.vod.model.session.VodBrowseCursorReference

interface VodBrowseCursorLocalStore {

    /** Attach the store interface to store of another user.
     */
    fun switchUser(userLoginName: String)

    /** Insert or update browsing cursor record (no browsing history recorded).
     */
    fun putBrowseCursor(cursor: VodBrowseCursor): Completable

    /** Get latest play cursor position for current user
     */
    fun getBrowseCursorReference(): Single<VodBrowseCursorReference?>

    /** Get latest browsing cursor position over all users
     */
    fun getMostRecentActivity(serviceId: Long): Single<UserActivityRecord?>
}