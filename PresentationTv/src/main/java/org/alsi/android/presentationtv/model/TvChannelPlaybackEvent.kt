package org.alsi.android.presentationtv.model

import org.alsi.android.domain.tv.model.guide.TvChannel

class TvChannelPlaybackEvent (
        val categoryId: Long,
        val channel: TvChannel
)