package org.alsi.android.moidom.model.vod

import org.alsi.android.moidom.model.base.BaseResponse
import org.alsi.android.moidom.model.base.RequestError


data class VodInfoResponse(
        val film: Film,
        override val error: RequestError?,
        override val servertime: Int

): BaseResponse() {

    data class Film(
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
            val rate_horror: Int,
            val videos: List<Video>,
            val genres: List<Genre>
    ) {

        data class Genre(
                val id: Int,
                val name: String
        )


        data class Video(
                val id: Int,
                val id_content: Int,
                val num: Int,
                val title: String,
                val format: String,
                val url: String,
                val size: Int,
                val codec: String,
                val lenght: Int,
                val width: Int,
                val height: Int,
                val track1_codec: String,
                val track1_lang: String,
                val track2_codec: String,
                val track2_lang: String,
                val track3_codec: String,
                val track3_lang: String,
                val exist: Int
        )
    }
}