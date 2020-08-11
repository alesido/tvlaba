package org.alsi.android.domain.tv.model.session

import org.alsi.android.domain.tv.model.guide.TvPlayback

class TvPlayCursor(
        var categoryId: Long,
        var playback: TvPlayback,
        var timeStamp: Long,
        var seekTime: Long
)