package org.alsi.android.domain.tv.model.guide

import java.net.URI

data class TvPromotionSet (
    val sections: List<TvPromotionSection>,
    val timeStamp: Long? = null
)

data class TvPromotionSection (
    val id: Long,
    val title: String,
    val programs: List<TvProgramPromotion>
)

data class TvProgramPromotion (
    val id: Long,
    val channelId: Long,
    val time: TvProgramTimeInterval? = null,
    val title: String,
    val slogan: String,
    val description: String,
    var mainPosterUri: URI? = null,
    var allPosterUris: List<URI>? = null,
) {
    var channel: TvChannel? = null
}
