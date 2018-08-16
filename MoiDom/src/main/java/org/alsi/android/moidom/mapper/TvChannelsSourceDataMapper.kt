package org.alsi.android.moidom.mapper

import android.text.format.DateUtils
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.domain.tv.model.guide.TvProgramDisposition
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.model.guide.TvProgramTimeInterval
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

                val dst = TvChannel(
                        id = src.id.toLong(),
                        categoryId = group.id.toLong(),
                        logoUri = iconPathMapper.uriFromPath(src.icon_path),
                        number = channelNumber++,
                        title = src.name)

                val channelHasSchedule = src.epg_progname != RestServiceMoidom.TOKEN_NO_EPG_CHANNEL

                with(dst) {
                    hasSchedule = channelHasSchedule
                    hasArchive = src.have_archive == 1
                    isPasswordProtected = src.protected != null && src.protected == 1
                    hasMultipleLanguageAudioTracks = src.audiotracks?.isEmpty()?: false
                }

                val live = TvProgramIssue(channelId = dst.id)
                with (live) {
                    time = TvProgramTimeInterval(
                            src.epg_start * DateUtils.SECOND_IN_MILLIS,
                            src.epg_end * DateUtils.SECOND_IN_MILLIS)
                    title = src.epg_progname
                    isTitleAvailable = channelHasSchedule
                    disposition = TvProgramDisposition.LIVE
                }
                dst.live = live

                dstChannels.add(dst)
            }
        }
        return dstChannels
    }
}