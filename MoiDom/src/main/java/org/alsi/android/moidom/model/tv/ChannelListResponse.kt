package org.alsi.android.moidom.model.tv

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class ChannelListResponse(
        val icons: List<Icon>,
        val groups: List<Group>,
        override val error: RequestError?,
        override val servertime: Int

): BaseResponse() {

    data class Group(
            val id: Int,
            val name: String,
            val color: String,
            val pos: Int,
            val channels: List<Channel>
    ) {

        data class Channel(
                val id: Int,
                val name: String,
                val pos: Int,
                val icon: String,
                val icon_path: String,
                val is_video: Int,
                val protected: Int?,
                val have_archive: Int,
                val is_favorite: Int,
                val audiotracks: List<String>?,
                val audiotrack_default: String?,
                val epg_progname: String,
                val epg_start: Int,
                val epg_end: Int,
                val epg_range: String,
                val hide: Int
        )
    }
}