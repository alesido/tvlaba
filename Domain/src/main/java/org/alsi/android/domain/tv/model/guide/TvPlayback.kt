package org.alsi.android.domain.tv.model.guide

import org.alsi.android.domain.streaming.model.VideoStream
import org.alsi.android.domain.tv.interactor.guide.TvProgramCredit
import java.net.URI

class TvPlayback (

    /** ID of a channel of a live or an archive played back
         */
        val channelId: Long,

    /** ID of an archive program or "null" for a channel w/o EPG
         */
        val programId: Long? = null,

    /** Stream for live or archive video. May be not set initially because an
         *  additional request required.
         */
        var stream: VideoStream? = null,

    /** Record of live stream given by the previous property
         */
        var record: VideoStream? = null,

    /** Program time interval, start and end time. N/A for channels w/o EPG.
         *
         * NOTE Looks like this is a duplicate, but it should simplify things because there are
         *  while similar still different data sets on live and archive program.
         */
        val time: TvProgramTimeInterval? = null,

    /** Title of the program item. N/A for channels w/o EPG.
         */
        val title: String? = null,

    /** Description of the program. Optional and may be N/A for channels w/o EPG.
         */
        val description: String? = null,

    /** ... initially it was added to repeat stream request after parental control authorization
         * (to reveal actual stream URL)
         *
         * This parameter is made required just to not forget to assign it when the object created
         */
        var isLive: Boolean = true,

    /** ... to indicate playback of live record
         */
        var isLiveRecord: Boolean? = null,

    /** Tels whether this like 18+ playback
         */
        val isUnderParentControl: Boolean = false,

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

    /** Current video playback position in milliseconds, a value from range [0, <playback duration>]
     */
    var position: Long = 0L

    fun hasScheduleLinked() = programId != null

    fun isEmpty() = channelId == -1L && stream == null

    /** Live program can be played as archive to allow seeking. To do so it is marked as RECORD.
     * This method is to check whether further seek forward is possible (not overruns "now"
     * position).
     *
     * @return True, if this seek step is not possible.
     */
    fun isSeekBeyondLiveEdge(seekTime: Long, interval: Long): Boolean {
        val now = System.currentTimeMillis()
        val s = time?.startUnixTimeMillis?: return false
        val e = time.endUnixTimeMillis
        return if (s > now || e < now) false else s + seekTime + interval > now // future and archive programs just skipped here
    }
    companion object {
            fun empty() = TvPlayback(channelId = -1L)
    }
}

enum class TvPlaybackState {
    INITIAL, PREPARING, SEEKING, BUFFERING, PLAYING, PAUSED, ERROR, ENDED
}