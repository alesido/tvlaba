package org.alsi.android.local.model.tv

import io.objectbox.annotation.*
import io.objectbox.relation.ToOne
import org.alsi.android.domain.tv.model.guide.TvProgramLive

@Entity
data class TvProgramLiveEntity (

        @Id var id: Long = 0L,

        var startMillis: Long? = null,
        var endMillis: Long? = null,

        var title: String? = null,

        var description: String? = null
) {
        fun updateWith(source: TvProgramLive) {
                startMillis = source.time?.startUnixTimeMillis
                endMillis = source.time?.endUnixTimeMillis
                title = source.title
                description = source.description
        }
}