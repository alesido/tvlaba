package org.alsi.android.moidom.model.vod

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class VodGenresResponse(
        val genres: List<Genre>,
): BaseResponse() {

    data class Genre(
            val id: String,
            val name: String,
            val count: Int?
    )
}