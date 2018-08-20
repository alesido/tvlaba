package org.alsi.android.moidom.mapper

import android.text.format.DateUtils
import org.alsi.android.domain.tv.model.guide.*
import org.alsi.android.moidom.model.tv.ChannelListResponse
import org.alsi.android.moidom.store.RestServiceMoidom
import org.alsi.android.remote.mapper.SourceDataMapper

class TvChannelsSourceDataMapper: SourceDataMapper<ChannelListResponse, List<TvChannel>> {

    override fun mapFromSource(source: ChannelListResponse): List<TvChannel> {
        val dstChannels: MutableList<TvChannel> = mutableListOf()
        val iconPathMapper = TvChannelIconPathMapper(source)
        var channelNumber = 1
        for (group in source.groups) {
            for (src in group.channels) {
                with (src) {

                    val channelId = id.toLong()
                    val channelHasSchedule = epg_progname != RestServiceMoidom.TOKEN_NO_EPG_CHANNEL

                    val dst = TvChannel(

                            id = channelId,
                            categoryId = group.id.toLong(),
                            logoUri = iconPathMapper.uriFromPath(icon_path),
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
                                    hasMultipleLanguageAudioTracks = audiotracks?.isEmpty()
                                            ?: false)
                    )

                    dstChannels.add(dst)
                }
            }
        }
        return dstChannels
    }
}