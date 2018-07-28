package org.alsi.android.moidom.model.remote.vod

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class VodListResponse(
        val type: String,
        val total: Int,
        val count: String,
        val page: String,
        val rows: List<Row>,
        override val error: RequestError?,
        override val servertime: Int

): BaseResponse() {

    data class Row(
            val id: Int,
            val is_tv_show: String,
            val project_id: Int,
            val id_mb: Int,
            val id_tmdb: Int,
            val enabled: Int,
            val dt_create: Int,
            val dt_modify: Int,
            val viewed: Int,
            val name: String,
            val name_orig: String,
            val description: String,
            val poster: String,
            val lenght: Int,
            val year: Int,
            val director: String,
            val scenario: String,
            val actors: String,
            val rate_imdb: Int,
            val rate_kinopoisk: Int,
            val rate_mpaa: Int,
            val country: String,
            val studio: String,
            val awards: String,
            val budget: Int,
            val images: String,
            val vis: String,
            val pass_protect: String,
            val genre_str: String,
            val rate_blood: Int,
            val rate_violence: Int,
            val rate_obscene: Int,
            val rate_porn: Int,
            val rate_horror: Int
    )
}