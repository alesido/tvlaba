package org.alsi.android.domain.tv.model.guide

import java.net.URI

class TvChannel (

        val id: Long,
        val categoryId: Long,
        val number: Int,
        var title: String? = null,
        var logoUri: URI? = null,
        var live: TvProgramLive,
        var features: TvChannelFeatures
) {
    fun isEmpty() = id == -1L

    companion object {
        fun empty() = TvChannel(-1L, -1L, -1,
            live = TvProgramLive(), features = TvChannelFeatures())
    }
}

typealias TvChannels = List<TvChannel>

class TvChannelsChange(
        val create: TvChannels = listOf(), // items to add
        val update: TvChannels = listOf(), // items to update
        val delete: TvChannels = listOf(), // items to delete
        val defect: TvChannels = listOf()  // items that are not up to date
) {
    val isEffective get()
    = create.isNotEmpty() || update.isNotEmpty() || delete.isNotEmpty()


    val isUpdateOnly get()
    = create.isEmpty() && delete.isEmpty()

    override fun toString(): String {
        return String.format("create %d, update %d, delete %d, defects %d",
                create.size, update.size, delete.size, defect.size)
    }
}

