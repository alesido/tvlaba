package org.alsi.android.moidom.model.vod

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class VodListResponse(
        val total: Int,
        var vods: List<Vod>,
): BaseResponse() {

    data class Vod(
            val id: Long,
            val name: String,
            val genre: String?,
            val image_url: String?,
            val country: String?,
            val description: String?,
    )
}