package org.alsi.android.presentationvod.model

import org.alsi.android.domain.vod.model.guide.listing.VodListingItem
import org.alsi.android.domain.vod.model.session.VodBrowseCursor

class VodDigestLiveData {
    var cursor: VodBrowseCursor? = null
    var details: VodListingItem? = null
    var updateScope = VodDigestUpdateScope.DIGEST
}

enum class VodDigestUpdateScope {
    DIGEST, LISTING
}