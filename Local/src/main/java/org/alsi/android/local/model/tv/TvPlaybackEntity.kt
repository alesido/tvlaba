package org.alsi.android.local.model.tv

import io.objectbox.annotation.*
import org.alsi.android.local.framework.objectbox.UriConverter
import java.net.URI

@Entity
data class TvPlaybackEntity (

        @Id var id: Long = 0L,

        @Index
        @Uid(1544790841200149238L)
        var channelId: Long = 0L,

        @Index
        var programId: Long = 0L,

        var start: Long = 0L,

        var end: Long = 0L,

        var title: String? = null,

        var description: String? = null,

        @Convert(converter = UriConverter::class, dbType = String::class)
        var streamUri: URI? = null
)
// @Backlink can only be used on a ToMany relation
//{
//        @Backlink
//        lateinit var channel: ToOne<TvPlayCursorEntity>
//}