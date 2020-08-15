package org.alsi.android.moidom.mapper

import org.alsi.android.domain.implementation.model.IconType
import org.alsi.android.domain.implementation.model.TypedIconReference
import org.alsi.android.moidom.model.tv.ChannelListResponse
import org.alsi.android.moidom.model.tv.EpgResponse
import org.alsi.android.moidom.model.tv.GetTvGroupResponse
import java.net.URI

class TvCategoryIconPathMapper() {

    private lateinit var baseUrl: String
    private lateinit var format: String

    constructor(response: GetTvGroupResponse): this() {
        val iconParams = response.groups_icons[0]
        baseUrl = iconParams.base_url
        format = iconParams.formats[0]
    }

    // TODO Ask to change the API so as Channel List Response will have configuration
    //  for category icons and "group" records will have icon path.
    constructor(response: ChannelListResponse): this() {
        val iconParams = response.icons[0]
        baseUrl = iconParams.base_url
        format = iconParams.formats[0]
    }

    fun fromPath(path: String): TypedIconReference
            = TypedIconReference(IconType.REMOTE_RASTER, "$baseUrl$path.$format")
}

class TvChannelIconPathMapper(response: ChannelListResponse) {

    private val iconParams = response.icons[2]
    private val baseUrl = iconParams.base_url
    private val imageTypeExtension = iconParams.formats[0]

    fun uriFromPath(path: String): URI = URI.create("$baseUrl$path.$imageTypeExtension")
}

class TvProgramPosterIconPathMapper(response: EpgResponse) {

    private val iconParams = response.icons[0]
    private val baseUrl = iconParams.base_url
    private val imageTypeExtension = iconParams.formats[0]

    fun uriFromPath(path: String): URI = URI.create("$baseUrl$path.$imageTypeExtension")

    fun fixUri(uriString: String?): URI {
        uriString?: return URI.create(baseUrl) // TODO Set default poster URI
        val fixedUriString = uriString.replaceFirst("http:", "https:")
        return URI.create(fixedUriString)
    }
}
