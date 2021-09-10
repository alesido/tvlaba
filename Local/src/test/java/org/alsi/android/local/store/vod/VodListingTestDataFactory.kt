@file:Suppress("MemberVisibilityCanBePrivate")

package org.alsi.android.local.store.vod

import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem.Credit
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem.Genre
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem.Role.*
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem.Track.*
import org.alsi.android.domain.vod.model.guide.listing.VodListingPage
import java.net.URI

object VodListingTestDataFactory {

    fun listingPage(variant: Int = 1): VodListingPage {
        val items: MutableList<VodListingItem> = mutableListOf()

        items.add(videoSingleItem(variant + 1))
        items.add(videoSingleItem(variant + 2))
        items.add(videoSingleItem(variant + 3))
        items.add(videoSerialItem(variant + 4))
        items.add(videoSerialItem(variant + 5))
        items.add(videoSerialItem(variant + 6))

        return VodListingPage(variant + 1L, variant + 2L, 345 + variant,
            variant, 3, items)
    }

    fun videoSingleItem(variant: Int): VodListingItem {
        val singleVideoId = variant + 10L
        return VodListingItem(id = variant.toLong(),
            sectionId = variant + 1L, unitId = variant + 2L, title = "VOD Item #$variant",
            description = "Description of VOD Item #$variant",
            video = VodListingItem.Video.Single(id = singleVideoId,
                title ="\"Video Single #$variant-$singleVideoId",
                description = "Description of Video Single #$variant-$singleVideoId",
                uri = URI.create(
                    "http://test.example.com/video/single?item=$variant&video=$singleVideoId"),
                tracks = listOf(
                    Audio(0L, "en", "English"),
                    Audio(0L, "fr", "French"),
                    Audio(0L, "de", "German"),
                    Subtitles(0L, "en", "English"),
                    Subtitles(0L, "fr", "French"),
                    Subtitles(0L, "de", "German"),
                    Subtitles(0L, "sp", "Spanish"),
                ),
                durationMillis = (3600L + variant) * 1000L
            ),
            posters = VodListingItem.Posters(
                poster = URI.create("http://test.example.com/poster/$variant/$singleVideoId/main"),
                gallery = listOf(
                    URI.create("http://test.example.com/poster/$variant/$singleVideoId/1"),
                    URI.create("http://test.example.com/poster/$variant/$singleVideoId/2"),
                    URI.create("http://test.example.com/poster/$variant/$singleVideoId/3"),
                )
            ),
            attributes = VodListingItem.Attributes(
                durationMillis = (3600L + variant) * 1000L,
                genres = listOf(
                    Genre(variant + 101L, "Genre $variant/$singleVideoId/1"),
                    Genre(variant + 102L, "Genre $variant/$singleVideoId/2"),
                ),
                credits = listOf(
                    Credit("Actor 1", ACTOR),
                    Credit("Actor 2", ACTOR),
                    Credit("Director 1", DIRECTOR),
                ),
                year = "1981",
                country = "Germany, Spain",
                ageLimit = "18+",
                kinopoiskRate = "8.5",
                imdbRate = "7.6"
            )
        )
    }

    fun videoSerialItem(variant: Int): VodListingItem {
        val serialVideoId = variant + 10L
        return VodListingItem(id = variant.toLong(),
            sectionId = variant + 1L, unitId = variant + 2L, title = "VOD Item #$variant",
            description = "Description of VOD Item #$variant",
            video = VodListingItem.Video.Serial(id = serialVideoId,
                title ="\"Video Serial #$variant-$serialVideoId",
                description = "Description of Video Single #$variant-$serialVideoId",
                series = listOf(
                    videoSeriesItem(variant + 10),
                    videoSeriesItem(variant + 20),
                    videoSeriesItem(variant + 30),
                )
            ),
            posters = VodListingItem.Posters(
                poster = URI.create("http://test.example.com/poster/$variant/main"),
                gallery = listOf(
                    URI.create("http://test.example.com/poster/$variant/1"),
                    URI.create("http://test.example.com/poster/$variant/2"),
                    URI.create("http://test.example.com/poster/$variant/3"),
                )
            ),
            attributes = VodListingItem.Attributes(
                durationMillis = (3600L + variant) * 1000L,
                genres = listOf(
                    Genre(variant + 103L, "Genre $variant/$serialVideoId/$variant/3"),
                    Genre(variant + 104L, "Genre $variant/$serialVideoId/$variant/4"),
                ),
                credits = listOf(
                    Credit("Actor 21", ACTOR),
                    Credit("Actor 22", ACTOR),
                    Credit("Director 31", DIRECTOR),
                ),
                year = (1981 + variant).toString(),
                country = "Spain, Italy",
                ageLimit = "12+",
                kinopoiskRate = "8.0",
                imdbRate = "4.6"
            )
        )
    }

    fun videoSeriesItem(variant: Int): VodListingItem.Video.Series {
        return VodListingItem.Video.Series(
            id = variant.toLong(),
            title = "Video Series #$variant",
            season = variant,
            episode = variant + 100,
            description = "Description of Video Series #$variant",
            uri = URI.create("http://test.example.com/video/series?item=$variant"),
            tracks = listOf(
                Audio(0L, "en", "English"),
                Subtitles(0L, "fr", "French"),
                Subtitles(0L, "de", "German"),
            ),
            durationMillis = (2700L + variant) * 1000
        )
    }
}