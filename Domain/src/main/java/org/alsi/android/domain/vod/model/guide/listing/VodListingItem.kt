package org.alsi.android.domain.vod.model.guide.listing

import java.net.URI

data class VodListingItem(

    val id: Long,

    val sectionId: Long? = null,
    val unitId: Long? = null,

    val title: String? = null,
    val description: String? = null,

    val video: Video? = null,
    val posters: Posters? = null,
    val attributes: Attributes? = null,

    val timeStamp: Long? = null
) {
    // Video

    sealed class Video {

        data class Single(
            val id: Long,
            val title: String? = null,
            val description: String? = null,
            val uri: URI? = null,
            val tracks: List<Track>? = null,
            val durationMillis: Long? = null
        ): Video()

        data class Series(
            val id: Long,
            val title: String?,
            val season: Int? = null, // in case there are no seasons, just multiple series
            val episode: Int? = null,
            val description: String? = null,
            val uri: URI? = null,
            val tracks: List<Track>? = null,
            val durationMillis: Long
        ): Video()

        data class Serial(
            val id: Long,
            val title: String? = null,
            val description: String? = null,
            val series: List<Series>
        ): Video()
    }

    // Track

    sealed class Track(
        val id: Long,
        val languageCode: String?,
        val title: String?
    ) {
        class Video(id: Long, languageCode: String?, title: String?): Track(id, languageCode, title)
        class Audio(id: Long, languageCode: String?, title: String?): Track(id, languageCode, title)
        class Subtitles(id: Long, languageCode: String?, title: String?): Track(id, languageCode, title)
    }

    // Posters

    data class Posters(
        val poster: URI? = null,
        val gallery: List<URI>? = null,
    )

    // Attributes

    data class Attributes(
        val durationMillis: Long? = null, // to be shown in the digest
        val genres: List<Genre>? = null,
        val credits: List<Credit>? = null,
        val year: String? = null,
        val country: String? = null,
        val quality: String? = null,
        val ageLimit: Int? = null,
        val kinopoiskRate: Float? = null,
        val imdbRate: Float? = null,
    )

    /**
     *  Genre rather corresponds to Unit. But, not all units represent genres, e.g. "Search Results" unit.
     *  And, it can be different in some VOD providers (Unit IDs do not linked to Genre IDs).
     *
     *  Multiple sections may contain units for the same genre. For example, section "TV Shows"
     *  and section "Movies" both can have unit "Comedy".
     *
     *  App, possibly, is may be required in future to support navigation by genre to units.
     *  Again, that is not for all providers.
     *
     *  Genre may not have ID at all in data model of a given VOD provider.
     */
    class Genre(val genreId: Long? = null, val title: String?)

    class Credit(
        val name: String?,
        val role: Role? = null,
        @Suppress("unused") val photoUris: List<URI>? = null
    )

    enum class Role {
        UNKNOWN, ACTOR, ARTIST, WRITER, COMPOSER, OPERATOR, PRODUCER, DIRECTOR
    }

    fun isEmpty() = id < 0L

    companion object {
        fun empty() = VodListingItem(-1L)
    }
}


