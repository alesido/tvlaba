package org.alsi.android.local.model.vod

import io.objectbox.annotation.*
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.local.framework.objectbox.UriConverter
import java.net.URI

@Entity
data class VodListingPageEntity (
    @Id var id: Long = 0L,

    var sectionId: Long? = null,
    var unitId: Long? = null,

    var total: Int? = 0,
    var start: Int = 0,

    var timeStamp: Long? = null
) {
    @Backlink(to = "listingPages")
    lateinit var items: ToMany<VodListingItemEntity>
}

@Entity
data class VodListingItemEntity (
    @Id(assignable = true) var id: Long = 0L,

    var title: String? = null,
    var description: String? = null,

    var timeStamp: Long? = null
) {
    lateinit var listingPages: ToMany<VodListingPageEntity>

    lateinit var videoSingle: ToOne<VideoSingleEntity>
    lateinit var videoSerial: ToOne<VideoSerialEntity>

    @Backlink lateinit var posters: ToMany<VodPosterEntity>
    lateinit var attributes: ToOne<VodAttributesEntity>
}

@Entity
data class VodListingOrderEntity (
    @Id var id: Long = 0L,

    @Index var pageId: Long? = null,
    @Index var itemId: Long? = null,

    var ordinal: Int? = null
)

@Entity
data class VideoSingleEntity(
    @Id(assignable = true) var id: Long = 0L,
    var title: String? = null,
    var description: String? = null,
    @Convert(converter = UriConverter::class, dbType = String::class)
    var uri: URI? = null,
    var durationMillis: Long? = null
) {
    @Backlink (to = "videoSingle")
    lateinit var tracks: ToMany<VodTrackEntity>
}

@Entity
data class VideoSerialEntity(
    @Id(assignable = true) var id: Long = 0L,
    var title: String? = null,
    var description: String? = null,
) {
    @Backlink lateinit var series: ToMany<VideoSeriesEntity>
}

@Entity
data class VideoSeriesEntity(
    @Id(assignable = true) var id: Long = 0L,
    var season: Int? = null, // in case there are no seasons, just multiple series
    var episode: Int? = null,
    var title: String? = null,
    var description: String? = null,
    @Convert(converter = UriConverter::class, dbType = String::class)
    var uri: URI? = null,
    var durationMillis: Long? = null
) {
    lateinit var videoSerial: ToOne<VideoSerialEntity>

    @Backlink (to = "videoSeries")
    lateinit var tracks: ToMany<VodTrackEntity>
}

@Entity
data class VodTrackEntity (
    @Id var id: Long = 0L,
    var type: Int? = null,
    var languageCode: String? = null,
    var title: String? = null,
) {
    lateinit var videoSingle: ToOne<VideoSingleEntity>
    lateinit var videoSeries: ToOne<VideoSeriesEntity>

    companion object {
        const val TRACK_VIDEO = 1
        const val TRACK_AUDIO = 2
        const val TRACK_SUBTITLES = 3
    }
}

@Entity
data class VodPosterEntity (
    @Id var id: Long = 0L,
    @Convert(converter = UriConverter::class, dbType = String::class)
    var uri: URI? = null,
) {
    lateinit var videoSingle: ToOne<VodListingItemEntity>
}

@Entity
data class VodAttributesEntity (
    @Id var id: Long = 0L,
    var durationMillis: Long? = null, // to be shown in the digest
    var year: String? = null,
    var country: String? = null,
    var quality: String? = null,
    var ageLimit: String? = null,
    var kinopoiskRate: String? = null,
    var imdbRate: String? = null,
) {
    @Backlink lateinit var genres: ToMany<VodGenreEntity>
    @Backlink lateinit var credits: ToMany<VodCreditEntity>
}

@Entity
data class VodGenreEntity(
    @Id var id: Long = 0L,
    var genreId: Long? = null,
    var title: String? = null
) {
    lateinit var vodAttributes: ToOne<VodAttributesEntity>
}

@Entity
data class VodCreditEntity(
    @Id var id: Long = 0L,
    var name: String? = null,
    @Convert(converter = VodCreditRoleConverter::class, dbType = Int::class)
    var role: VodListingItem.Role? = null
) {
    lateinit var vodAttributes: ToOne<VodAttributesEntity>
}
