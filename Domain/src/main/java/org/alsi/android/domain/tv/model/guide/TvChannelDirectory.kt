package org.alsi.android.domain.tv.model.guide

class TvChannelDirectory(
        // list of channel categories
        val categories: List<TvChannelCategory>,
        // ordered list all channels
        val channels: List<TvChannel>,
        // index maps category to list of channels belonging to it by category ID
        val index: Map<Long, List<TvChannel>>
) {
    fun categoryIndex(category: TvChannelCategory): Int
            = categories.indexOfFirst{ it.id == category.id }

    fun channelIndex(channel: TvChannel): Int?
            = index[channel.categoryId]?.indexOfFirst{ it.id == channel.id }
}