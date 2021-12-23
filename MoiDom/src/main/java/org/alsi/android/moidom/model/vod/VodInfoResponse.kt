package org.alsi.android.moidom.model.vod

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class VodInfoResponse(

        val id: Long,
        val name: String,
        val description: String?,
        val duration: Int?, // minutes

        val genre: String?,
        val image_url: String?,
        val year: String?,
        val country: String?,
        val min_age: Int?,

        val actors: String?,
        val directors: String?,
): BaseResponse()