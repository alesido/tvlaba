package org.alsi.android.moidom.model.remote.tv

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class GetUrlResponse(
        val url: String,
        val mode: String,
        val ip_addr: String,
        override val error: RequestError?,
        override val servertime: Int

): BaseResponse()
