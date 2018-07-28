package org.alsi.android.moidom.model.remote

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class SettingsSetResponse(
        val text: String,
        val code: Int,
        override val error: RequestError?,
        override val servertime: Int

): BaseResponse()
