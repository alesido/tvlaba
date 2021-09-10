package org.alsi.android.local.model.vod

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.local.framework.objectbox.UriConverter
import org.alsi.android.local.model.tv.VideoStreamKindConverter
import java.net.URI

@Entity
data class VodVideoSingleStreamEntity(
    @Id var id: Long = 0L,

    @Index var vodItemId: Long? = null,

    @Convert(converter = UriConverter::class, dbType = String::class)
    var streamUri: URI? = null,

    @Convert(converter = UriConverter::class, dbType = String::class)
    var subtitlesUri: URI? = null,

    @Index
    var timeStamp: Long = 0L,
)

@Entity
data class VodVideoSeriesStreamEntity(
    @Id var id: Long = 0L,

    @Index var seriesId: Long? = null,

    @Convert(converter = UriConverter::class, dbType = String::class)
    var streamUri: URI? = null,

    @Convert(converter = UriConverter::class, dbType = String::class)
    var subtitlesUri: URI? = null,

    @Index
    var timeStamp: Long = 0L,
)