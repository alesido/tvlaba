package org.alsi.android.local.model.vod

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.local.framework.objectbox.UriConverter
import org.alsi.android.local.model.tv.VideoStreamKindConverter
import java.net.URI

data class VodVideoStreamEntity(
    @Id var id: Long = 0L,

    @Convert(converter = UriConverter::class, dbType = String::class)
    var streamUri: URI? = null,

    @Convert(converter = VideoStreamKindConverter::class, dbType = Int::class)
    var streamKind: VideoStreamKind = VideoStreamKind.UNKNOWN,

    @Convert(converter = UriConverter::class, dbType = String::class)
    var subtitlesUri: URI? = null,

    @Index
    var timeStamp: Long = 0L,
)