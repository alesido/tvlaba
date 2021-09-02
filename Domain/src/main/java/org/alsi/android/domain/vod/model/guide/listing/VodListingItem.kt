package org.alsi.android.domain.vod.model.guide.listing

import java.net.URI

data class VodListingItem(

    val id: Long,

    val sectionId: Long,
    val unitId: Long,

    val title: String,
    val description: String? = null,

    val posters: Posters? = null,

    val video: Video? = null,

    val attributes: Attributes? = null
) {
    // Video

    sealed class Video() {

        data class Single(
            val id: Long,
            val title: String,
            val description: String? = null,
            val uri: URI? = null,
            val tracks: List<Track>? = null,
            val durationMillis: Long? = null
        ): Video()

        data class Series(
            val id: Long,
            val title: String,
            val season: Int? = null, // in case there are no seasons, just multiple series
            val episode: Int? = null,
            val description: String? = null,
            val uri: URI? = null,
            val tracks: List<Track>? = null,
            val durationMillis: Long
        ): Video()

        data class Serial(
            val id: Long,
            val title: String,
            val description: String? = null,
            val series: List<Series>
        ): Video()
    }

    // Track

    sealed class Track(
        val id: Long,
        val languageCode: String,
        val title: String?
    ) {
        class Video(id: Long, languageCode: String, title: String?): Track(id, languageCode, title)
        class Audio(id: Long, languageCode: String, title: String?): Track(id, languageCode, title)
        class Subtitles(id: Long, languageCode: String, title: String?): Track(id, languageCode, title)
    }

    // Posters

    data class Posters(
        val poster: URI? = null,
        val gallery: List<URI>? = null,
    )

    // Attributes

    data class Attributes(
        val durationMillis: Long? = null, // to be shown in the digest
        val credits: List<Credit>? = null,
        val year: String? = null,
        val country: String? = null,
        val genres: List<Genre>? = null,
        val quality: String? = null,
        val ageLimit: String? = null,
        val kinopoiskRate: String? = null,
        val imdbRate: String? = null,
    )

    class Genre(val id: Int, val title: String)

    class Credit(
        val name: String,
        val role: Role? = null,
        @Suppress("unused") val photoUris: List<URI>? = null
    )

    enum class Role {
        UNKNOWN, ACTOR, ARTIST, WRITER, COMPOSER, OPERATOR, PRODUCER, DIRECTOR
    }
}


