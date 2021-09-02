package org.alsi.android.domain.vod.model.session

import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem


/**
 * To track playback to support back navigation and restoring upon app restart,
 * to record watch history.
 *
 * Contains not IDs but full object references for section, unit, etc. as it used for navigation
 * in the run time. However, they are mapped to IDs while saved to local storage.
 */
class VodSearchCursor(

        /**
         * Search query text.
         */
        val text: String,

        /**
         * VOD Directory Section where the search performed. If "null" global search assumed.
         */
        val section: VodSection? = null,

        /**
         * VOD Directory Unit (subsection) where the search performed. If "null" the section scope
         * search assumed.
         */
        val unit: VodUnit? = null,

        /**
         * Position of a selected search result.
         */
        val listingPosition: Int? = null,

        /**
         * Selected search result
         */
        var listingItem: VodListingItem? = null,
)