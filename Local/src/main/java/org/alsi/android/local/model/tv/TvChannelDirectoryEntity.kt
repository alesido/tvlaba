package org.alsi.android.local.model.tv

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne
import org.alsi.android.local.model.user.SubscriptionPackageEntity

/** Introduced solely to check whether the local copy have to be reloaded from the remote
 *  upon the language change.
 *
 *  As we have a single copy of directory for all users it is superfluous to have one-to many
 *  relations to categories and channels here. Anyway, in case it needed they may be added right
 *  here.
 *
 */
@Entity
data class TvChannelDirectoryEntity(

        @Id var id: Long = 0L,

        /** language code, 2 symbols: identifies content
         */
        var language: String? = null,

        /** time shift hours: identifies content
         */
        var timeShift: Int? = 0,

        /** time stamp of the record
         */
        var timeStamp: Long = 0L,
) {
        lateinit var subscriptionPackage: ToOne<SubscriptionPackageEntity>

        @Backlink
        lateinit var categories: ToMany<TvChannelCategoryEntity>

        @Backlink
        lateinit var channels: ToMany<TvChannelEntity>

        /** relation to link channels and categories: a channel may belong to several categories
         */
        @Backlink
        lateinit var index: ToMany<TvChannelIndexEntity>
}