package org.alsi.android.presentationtv.model

import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectoryPosition
import org.alsi.android.domain.tv.model.guide.TvProgramPromotion
import org.alsi.android.domain.tv.model.guide.TvPromotionSet

class TvChannelDirectoryBrowseLiveData (
    val directory: TvChannelDirectory,
    val promotions: TvPromotionSet?,
    val position: TvChannelDirectoryPosition? = null
)