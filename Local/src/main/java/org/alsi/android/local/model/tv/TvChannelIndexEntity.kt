package org.alsi.android.local.model.tv

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.relation.ToOne

@Entity
data class TvChannelIndexEntity (

        @Id var id: Long = 0L,

        @Index
        var categoryId: Long = 0L,

        var channelId: Long = 0L
) {
        lateinit var directory: ToOne<TvChannelDirectoryEntity>
}