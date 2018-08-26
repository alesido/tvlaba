package org.alsi.android.moidom.mapper

import org.alsi.android.domain.implementation.model.IconType
import org.alsi.android.domain.implementation.model.TypedIconReference
import org.alsi.android.moidom.model.tv.ChannelListResponse
import org.alsi.android.moidom.model.tv.GetTvGroupResponse
import java.net.URI

class TvCategoryIconPathMapper(response: GetTvGroupResponse) {

    private val iconParams = response.groups_icons[0]
    private val baseUrl = iconParams.base_url
    private val format = iconParams.formats[0]

    fun fromPath(path: String): TypedIconReference
            = TypedIconReference(IconType.REMOTE_RASTER, "$baseUrl$path.$format")
}

class TvChannelIconPathMapper(response: ChannelListResponse) {

    private val iconParams = response.icons[2]
    private val baseUrl = iconParams.base_url
    private val format = iconParams.formats[0]

    fun uriFromPath(path: String): URI = URI.create("$baseUrl$path.$format")
}
