package org.alsi.android.moidom.model

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class SettingsSetResponse(
        val text: String,
        val code: Int,
): BaseResponse()
