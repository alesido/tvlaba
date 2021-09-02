package org.alsi.android.domain.vod.model.session

import org.alsi.android.domain.vod.model.guide.directory.VodSection
import org.alsi.android.domain.vod.model.guide.directory.VodUnit
import org.alsi.android.domain.vod.model.guide.listing.VodListingItem


/**
 * To track playback, to support back navigation and restoring upon app restart,
 * to record watch history.
 *
 * Contains not IDs but full object references for section, unit, etc. as it used for navigation
 * in the run time. However, they are mapped to IDs while saved to local storage.
 */
class VodBrowseCursor(
        val section: VodSection? = null,
        val unit: VodUnit? = null,
        val listingPosition: Int? = null,
        val item: VodListingItem? = null,
)