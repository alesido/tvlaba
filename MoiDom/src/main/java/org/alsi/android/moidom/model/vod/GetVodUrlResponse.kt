package org.alsi.android.moidom.model.vod

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class GetVodUrlResponse(
        val url: String,
): BaseResponse()
