package org.alsi.android.local.model.tv

import io.objectbox.annotation.*
import io.objectbox.relation.ToOne

@Entity
data class TvProgramLiveEntity (

        @Id var id: Long = 0L,

        var startMillis: Long? = null,
        var endMillis: Long? = null,

        var title: String? = null,

        var description: String? = null
) {
        @Backlink
        lateinit var channel: ToOne<TvChannelEntity>
}