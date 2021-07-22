package org.alsi.android.domain.tv.model.guide

import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.tv.interactor.guide.TvProgramCredit
import java.net.URI

class TvPlayback (

        /** ID of a channel of a live or an archive played back
         */
        val channelId: Long,

        /** ID of an archive program or "null" for a live, which identified by a channel
         */
        val programId: Long? = null,

        /** Live or archive stream
         */
        var stream: VideoStream?,

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
        val description: String? = null,

        // -- posters

        /** Posters. Main
         */
        var mainPosterUri: URI? = null,

        /** Posters. All
         */
        var allPosterUris: List<URI>? = null,


        // -- movie

        /** Movie. Season
         */
        var season: Int? = null,

        /** Movie. Series
         */
        var series: Int? = null,


        // -- details

        var releaseDates: String? = null,
        var languageCode: String? = null,
        var ageGroup: Int? = null,
        var categoryNames: List<String>? = null,
        var countryNames: List<String>? = null,
        var awards: String? = null,
        var production: String? = null,
        var credits: List<TvProgramCredit>? = null, // credits to actors, writers, composers, producers, directors
        var rateKinopoisk: Float? = null,
        var rateImdb: Float? = null,

        val channelNumber: Int? = null,
        var channelTitle: String? = null,
        var channelLogoUri: URI? = null
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


   fun isEmpty() = channelId == -1L && stream == null

    companion object {
            fun empty() = TvPlayback(channelId = -1L, stream = null)
    }
}

enum class TvPlaybackState {
    INITIAL, PREPARING, SEEKING, BUFFERING, PLAYING, PAUSED, ERROR, ENDED
}