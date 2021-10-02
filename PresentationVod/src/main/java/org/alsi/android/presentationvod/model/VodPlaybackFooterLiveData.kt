package org.alsi.android.presentationvod.model

import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.session.VodBrowseCursor

class VodPlaybackFooterLiveData {
    var cursor: VodBrowseCursor? = null
    var details: VodListingItem? = null
    var updateScope = VodPlaybackFooterUpdateScope.PLAYBACK_DETAILS

}

enum class VodPlaybackFooterUpdateScope {
    PLAYBACK_DETAILS, LISTING, SERIES
}
