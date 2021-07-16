package org.alsi.android.datatv.store

import io.reactivex.Completable
import io.reactivex.Single
import org.alsi.android.domain.context.model.UserActivityRecord
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvBrowseCursorReference

interface TvBrowseCursorLocalStore {

    /** Attach the store interface to store of another user.
     */
    fun switchUser(userLoginName: String)

    /** Insert or update browsing cursor record (no browsing history recorded).
     */
    fun putBrowseCursor(cursor: TvBrowseCursor): Completable

    /** Get latest play cursor position for current user
     */
    fun getBrowseCursorReference(): Single<TvBrowseCursorReference?>

    /** Get latest browsing cursor position over all users
     */
    fun getMostRecentActivity(): Single<UserActivityRecord?>
}