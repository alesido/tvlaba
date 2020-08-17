package org.alsi.android.domain.tv.model.guide

import java.net.URI

class TvPlayback (

        /** ID of a channel of a live or an archive played back
         */
        val channelId: Long,

        /** ID of an archive program or "null" for a live, which identified by a channel
         */
        val programId: Long? = null,

        /** Live or archive stream URI
         */
        var streamUri: URI?,

        /** Program time interval, start and end time. N/A for channels w/o EPG.
         *
         * NOTE Seems this are duplicated data, but it should simplify things
         * 'cause we have similar but different data sets on live and archive program.
         */
        val time: TvProgramTimeInterval? = null,

        /** Title of the program item. N/A for channels w/o EPG.
         */
        val title: String? = null,

        /** Description of the program. Optional and may be N/A for channels w/o EPG.
         */
        val description: String? = null
) {

    /** Current playback disposition, i.e. indication of availability and type of the video stream
     */
    val disposition: TvProgramDisposition get() = evaluateTvProgramDisposition(time)

    /** The playback state
     */
    var state: TvPlaybackState = TvPlaybackState.INITIAL

    /** Current video playback position.
     */
    var position: Long = 0L
}

enum class TvPlaybackState {
    INITIAL, PREPARING, SEEKING, BUFFERING, PLAYING, PAUSED, ERROR, ENDED
}