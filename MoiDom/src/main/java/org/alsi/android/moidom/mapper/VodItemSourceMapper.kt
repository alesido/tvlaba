package org.alsi.android.moidom.mapper

import android.text.format.DateUtils
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.moidom.model.vod.VodInfoResponse
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.UNKNOWN_UNIT_ID
import org.alsi.android.moidom.store.RestServiceMoidom.Companion.VOD_SECTION_ID
import org.alsi.android.remote.mapper.SourceDataMapper
import java.net.URI

class VodItemSourceMapper: SourceDataMapper<VodInfoResponse, VodListingItem> {

    override fun mapFromSource(source: VodInfoResponse): VodListingItem {
        // videos
        val dstVideo = with(source) { VodListingItem.Video.Single(
            id,
            title = name,
            durationMillis = duration?.let { it * DateUtils.MINUTE_IN_MILLIS }
        )}

        // credits
        val dstCredits: MutableList<VodListingItem.Credit> = mutableListOf()
        with(source) {
            actors?.let { actors ->
                actors.split(",")
                    .forEach {
                        dstCredits.add(
                            VodListingItem.Credit(
                                name = it.trim(),
                                role = VodListingItem.Role.ACTOR
                            )
                        )
                    }
            }
            directors?.let { directors ->
                directors.split(",")
                    .forEach {
                        dstCredits.add(
                            VodListingItem.Credit(
                                name = it.trim(),
                                role = VodListingItem.Role.DIRECTOR
                            )
                        )
                    }
            }
        }

        // attributes
        val dstAttributes = with(source) { VodListingItem.Attributes(
            durationMillis = duration?.let { it * DateUtils.MINUTE_IN_MILLIS },
            credits = dstCredits,
            genres = genre?.let { listString -> listString.split(",")
                .mapIndexed { index, name -> VodListingItem.Genre(index + 1, name) }
            },
            year = year,
            country = country,
            ageLimit = min_age.toString() + "+"
        )}

        return with(source) { VodListingItem(
            id, VOD_SECTION_ID, UNKNOWN_UNIT_ID,
            title = name,
            description = description,
            posters = image_url?.let { VodListingItem.Posters(poster = URI.create(it)) },
            video = dstVideo,
            attributes = dstAttributes
        )}
    }
}