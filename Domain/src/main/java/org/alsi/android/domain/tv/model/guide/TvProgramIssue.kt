package org.alsi.android.domain.tv.model.guide

import org.alsi.android.domain.tv.interactor.guide.TvProgramCredit
import org.alsi.android.domain.tv.model.guide.TvProgramDisposition.*
import java.net.URI
import java.util.concurrent.TimeUnit

/**
 *  TV program issue.
 *
 *  A particular broadcasting event, a show, a movie, etc. on a certain TV channel
 *  at a certain date and time.
 *
 */
class TvProgramIssue(

        // -- core

        /** ID of a TV channel where the program have been issued
         */
        val channelId: Long,

        /** Program time interval, start and end time. N/A for channels w/o EPG.
         */
        var time: TvProgramTimeInterval? = null,

        /** ID of a TV program issue
        */
        var programId: Long? = time?.startUnixTimeMillis,

        /** Title of the program item. N/A for channels w/o EPG.
        */
        var title: String? = null,

        /** Description of the program. Optional and may be N/A for channels w/o EPG.
        */
        var description: String? = null,

        /** Indicates availability of the issue record (?)
         */
        var hasArchive: Boolean = true,

        /** Some current channel programs w/o EPG has no title. This indicates it explicitly.
         */
        var isTitleAvailable: Boolean = true,

        /** ... no used actually, should be rather removed
         */
        var timeShiftMillis: Long = 0L,

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


    // -- state

        /** The playback state
        */
        var state: TvPlaybackState = TvPlaybackState.INITIAL,

        /** Current video playback position.
        */
        var position: Long = 0L
) {
        @Suppress("MemberVisibilityCanBePrivate")
        val disposition: TvProgramDisposition get() = evaluateTvProgramDisposition(time)
}

fun evaluateTvProgramDisposition(programTime: TvProgramTimeInterval?): TvProgramDisposition {
        val nowMillis = System.currentTimeMillis()
        val reserve = TimeUnit.SECONDS.toMillis(1)
        programTime?: return LIVE
        return when {
                programTime.endUnixTimeMillis < nowMillis - reserve -> RECORD
                programTime.startUnixTimeMillis > nowMillis + reserve -> FUTURE
                else -> LIVE
        }
}