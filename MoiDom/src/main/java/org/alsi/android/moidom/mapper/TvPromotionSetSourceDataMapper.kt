package org.alsi.android.moidom.mapper

import org.alsi.android.domain.tv.model.guide.TvProgramPromotion
import org.alsi.android.domain.tv.model.guide.TvProgramTimeInterval
import org.alsi.android.domain.tv.model.guide.TvPromotionSection
import org.alsi.android.domain.tv.model.guide.TvPromotionSet
import org.alsi.android.moidom.model.tv.GetPromotionsResponse
import java.net.URI
import java.util.concurrent.TimeUnit

class TvPromotionSetSourceDataMapper {

    fun mapFromSource(source: GetPromotionsResponse): TvPromotionSet {

        return TvPromotionSet(
            sections = source.promotions.map { section ->
                     TvPromotionSection(
                         section.id,
                         if (section.name == "Анонсы") "Рекомендации" else section.name,
                         section.programs.map {
                             TvProgramPromotion(
                                 id = it.id,
                                 channelId = it.cid,
                                 time = TvProgramTimeInterval(
                                     startUnixTimeMillis = TimeUnit.SECONDS.toMillis(it.ut_start),
                                     endUnixTimeMillis = TimeUnit.SECONDS.toMillis(it.ut_stop)
                                 ),
                                 title = it.title,
                                 slogan = it.name,
                                 description = it.description,
                                 mainPosterUri = if (it.images.isNotEmpty())
                                     URI.create(it.images[0]) else null,
                                 allPosterUris = it.images.map {
                                         uriString -> URI.create(uriString)
                                 }
                             )
                         }
                     )
            },
            timeStamp = source.servertime?.toLong()?.times(1000)?: System.currentTimeMillis()
        )
    }
}