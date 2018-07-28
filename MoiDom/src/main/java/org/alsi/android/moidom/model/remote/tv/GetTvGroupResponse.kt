package org.alsi.android.moidom.model.remote.tv

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class GetTvGroupResponse(
        val lang: String,
        val template: String,
        val groups_icons: List<GroupsIcon>,
        val groups: List<Group>,
        override val error: RequestError?,
        override val servertime: Int

): BaseResponse() {

    data class Group(
            val id: Int,
            val name: String,
            val color: String,
            val icon_path: String,
            val channels_count: Int
    )


    data class GroupsIcon(
            val size: String,
            val base_url: String,
            val formats: List<String>
    )
}