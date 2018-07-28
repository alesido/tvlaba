package org.alsi.android.moidom.model.remote.vod

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class GetVodUrlResponse(
        val url: String,
        override val error: RequestError?,
        override val servertime: Int

): BaseResponse()
