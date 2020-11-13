package org.alsi.android.local.model.tv

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.converter.PropertyConverter
import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.local.framework.objectbox.UriConverter
import java.net.URI

@Entity
data class TvVideoStreamEntity (

    @Id
    var id: Long = 0L,

        /** TV channel ID in the API level.
     *  It's to identify a live stream which this record references while there is no ID of
     *  an archive program defined here (if this record references a live stream, program ID
     *  field value is 0L)
     */
    @Index
    var channelId: Long = 0L,

        /** TV program ID in the API level.
     */
    @Index
    var programId: Long = 0L, // to identify an archive record

    @Convert(converter = UriConverter::class, dbType = String::class)
    var streamUri: URI? = null,

    @Convert(converter = VideoStreamKindConverter::class, dbType = Int::class)
    var streamKind: VideoStreamKind = VideoStreamKind.UNKNOWN,

    @Convert(converter = UriConverter::class, dbType = String::class)
    var subtitlesUri: URI? = null,

    @Index
    var timeStamp: Long = 0L,

        /** Optional parent control code
     */
    var accessCode: String?,

        /** Stream start time.
     *  Along with the "end" defines a time interval for which the stream is valid.
     */
    var start: Long = 0L, // along with "end" define a time interval for which th stream i valid

        /** Stream end time.
     *  It's here also to support the stream URL expiration scheme.
     */
    var end: Long = 0L, //

        /** Auxiliary title. Just to ease debugging and reporting.
     */
    var title: String? = null
)
