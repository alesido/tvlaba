package org.alsi.android.presentationtv.model

import org.alsi.android.domain.tv.model.guide.TvChannelDirectory
import org.alsi.android.domain.tv.model.guide.TvChannelDirectoryPosition

class TvChannelDirectoryBrowseLiveData (
    val directory: TvChannelDirectory,
    val position: TvChannelDirectoryPosition? = null
)