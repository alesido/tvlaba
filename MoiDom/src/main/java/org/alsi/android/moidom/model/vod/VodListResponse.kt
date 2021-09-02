package org.alsi.android.moidom.model.vod

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class VodListResponse(
        val total: Int,
        val vods: List<Vod>,
        override val error: RequestError?,
        override val servertime: Int

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