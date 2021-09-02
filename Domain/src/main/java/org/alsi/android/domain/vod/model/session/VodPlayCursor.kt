package org.alsi.android.domain.vod.model.session

import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem

/**
 *
 */
class VodPlayCursor(

        /**
         *  VOD Directory Section to which the played back item belongs.
         */
        val section: VodSection? = null,

        /**
         * VOD Directory Unit (subsection) to which the played back item belongs.
         */
        val unit: VodUnit? = null,

        /**
         *  Unit/Subsection listing position of the played back item.
         */
        val listingPosition: Int? = null,

        /**
         * Played back listing item.
         */
        val listingItem: VodListingItem? = null,

        /**
         * Played back video of the listing item.
         */
        val video: VodListingItem.Video? = null
)