package org.alsi.android.domain.tv.model.guide

import org.alsi.android.domain.user.model.SubscriptionPackage

data class TvChannelDirectory(
    /** list of channel categories
    */
    val categories: List<TvChannelCategory>,

    /** ordered list of all channels
    */
    val channels: List<TvChannel>,

    /** index maps category to list of channels belonging to it by their category ID
    */
    val index: Map<Long, List<TvChannel>>,

    /** content of TV directory depends on channel packets selected in the subscription package
     */
    val subscriptionPackage: SubscriptionPackage,

    /** language code, 2 symbols: identifies content
    */
    val language: String,

    /** time shift hours: identifies content
    */
    val timeShift: Int,

) {
    val categoryById = categories.map { it.id to it }.toMap()

    val channelById = channels.map { it.id to it }.toMap()

    /** changes from the last update
     */
    var change: TvChannelsChange? = null

    fun categoryIndex(category: TvChannelCategory): Int
            = categories.indexOfFirst{ it.id == category.id }

    fun channelIndex(channel: TvChannel): Int?
            = index[channel.categoryId]?.indexOfFirst{ it.id == channel.id }

    val isChanged get() = change != null && change?.isEffective == true

    fun isEmpty() = categories.isEmpty()

    companion object {
        fun empty() = TvChannelDirectory(listOf(), listOf(), mapOf(), SubscriptionPackage(0L),"en", 0)
    }
}