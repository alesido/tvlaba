package org.alsi.android.local.mapper.tv

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.local.model.tv.TvChannelEntity

class TvChannelEntityMapper: EntityMapper<TvChannelEntity, TvChannel> {

    private val liveMapper = TvProgramLiveEntityMapper()

    private val featuresMapper = TvChannelFeaturesEntityMapper()

    override fun mapFromEntity(entity: TvChannelEntity): TvChannel {
        return with(entity) {
            val channel = TvChannel(id, categoryId,number?: (id + 1).toInt(), title, logoUri,
                    liveMapper.mapFromEntity(live.target),
                    featuresMapper.mapFromEntity(features.target))
            channel
        }
    }

    override fun mapToEntity(domain: TvChannel): TvChannelEntity {
        return with(domain) {
            val entity = TvChannelEntity(id, categoryId, logoUri, number, title)
            entity.live.target = liveMapper.mapToEntity(live)
            entity.features.target = featuresMapper.mapToEntity(domain.features)
            entity
        }
    }
}