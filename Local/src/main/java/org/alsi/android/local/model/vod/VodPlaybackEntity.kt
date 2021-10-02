package org.alsi.android.local.model.vod

import io.objectbox.annotation.*
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.local.framework.objectbox.UriConverter
import org.alsi.android.local.model.tv.VideoStreamKindConverter
import java.net.URI

@Entity
data class VodPlaybackEntity (

        @Id var id: Long = 0L,

        @Index var sectionId: Long = 0L,
        @Index var unitId: Long = 0L,
        @Index var itemId: Long = 0L,
        var seriesId: Long? = null,

        var title: String? = null,
        var description: String? = null,
        var season: Int? = null,
        var series: Int? = null,

        @Convert(converter = UriConverter::class, dbType = String::class)
        var streamUri: URI? = null,

        @Convert(converter = VideoStreamKindConverter::class, dbType = Int::class)
        var streamKind: VideoStreamKind = VideoStreamKind.RECORD,

        @Convert(converter = UriConverter::class, dbType = String::class)
        var subtitlesUri: URI? = null
)