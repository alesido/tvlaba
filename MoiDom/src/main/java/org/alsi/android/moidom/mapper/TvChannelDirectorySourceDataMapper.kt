package org.alsi.android.moidom.mapper

import android.text.format.DateUtils
import okhttp3.internal.toImmutableMap
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.moidom.model.tv.ChannelListResponse
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.remote.mapper.SourceDataMapper

class TvChannelDirectorySourceDataMapper: SourceDataMapper<ChannelListResponse, TvChannelDirectory> {

    override fun mapFromSource(source: ChannelListResponse): TvChannelDirectory {

        // list of categories
        val dstCategories: MutableList<TvChannelCategory> = mutableListOf()

        // map of all channels by channel ID (to collect unique channels to a common list)
        val dstChannels: MutableMap<Long, TvChannel> = mutableMapOf()

        // map of channel lists by category ID (a channel may belong to multiple categories)
        val dstCategoryChannels: MutableMap<Long, List<TvChannel>> = mutableMapOf()

        val channelIconPathMapper = TvChannelIconPathMapper(source)

        var channelNumber = 1
        for (group in source.groups) {

            dstCategories.add(
                    TvChannelCategory(id = group.id.toLong(), title = group.name, logo = null))

            val thisCategoryChannels: MutableList<TvChannel> = mutableListOf()
            for (srcChannel in group.channels) {
                with (srcChannel) {

                    val channelId = id.toLong()
                    val channelHasSchedule = epg_progname != RestServiceMoidom.TOKEN_NO_EPG_CHANNEL

                    val dstChannel = TvChannel(

                            id = channelId,
                            categoryId = group.id.toLong(),
                            logoUri = channelIconPathMapper.uriFromPath(icon_path),
                            number = channelNumber++,
                            title = name,

                            live = TvProgramLive(
                                    time = TvProgramTimeInterval(
                                            epg_start * DateUtils.SECOND_IN_MILLIS,
                                            epg_end * DateUtils.SECOND_IN_MILLIS),
                                    title = epg_progname),

                            features = TvChannelFeatures(
                                    hasSchedule = channelHasSchedule,
                                    hasArchive = have_archive == 1,
                                    isPasswordProtected = protected != null && protected == 1,
                                    hasMultipleLanguageAudioTracks = audiotracks?.isEmpty()?: false)
                    )

                    if (! dstChannels.contains(dstChannel.id)) {
                        dstChannels[dstChannel.id] = dstChannel
                    }

                    thisCategoryChannels.add(dstChannel)
                }
            }

            dstCategoryChannels[group.id.toLong()] = thisCategoryChannels
        }

        return TvChannelDirectory(
                categories = dstCategories,
                channels = dstChannels.values.toList(),
                index = dstCategoryChannels.toImmutableMap())
    }
}