package org.alsi.android.local.model.tv

import io.objectbox.annotation.*
import org.alsi.android.domain.streaming.model.VideoStreamKind
import org.alsi.android.local.framework.objectbox.UriConverter
import java.net.URI

@Entity
data class TvPlaybackEntity (

        @Id var id: Long = 0L,

        /** ID of the playback's TV channel.
         *
         */
        @Index
        var channelId: Long = 0L,

        /** ID of program in TV schedule. Null, if the channel has no schedule.
         */
        @Index
        var programId: Long? = null,

        /** Program's start time in millis since epoch. Null, if the channel has no schedule.
         */
        var start: Long? = null,

        /** Program's start time in millis since epoch. Null, if the channel has no schedule.
         */
        var end: Long? = null,

        /** Title of played back program
         */
        var title: String? = null,

        /** Description of played back program
         */
        var description: String? = null,

        /** Tells, whether this playback intended to play as live (true) or archive (false)
         */
        var isLive: Boolean = true,

        /** Tells, whether access to this playback is protected with parental control password.
         */
        var isUnderParentControl: Boolean = false,

        @Convert(converter = UriConverter::class, dbType = String::class)
        var streamUri: URI? = null,

        @Convert(converter = VideoStreamKindConverter::class, dbType = Int::class)
        var streamKind: VideoStreamKind = VideoStreamKind.UNKNOWN,

        @Convert(converter = UriConverter::class, dbType = String::class)
        var subtitlesUri: URI? = null
)