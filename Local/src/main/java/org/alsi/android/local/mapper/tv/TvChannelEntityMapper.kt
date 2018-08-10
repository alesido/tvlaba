package org.alsi.android.local.mapper.tv

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.tv.model.guide.TvChannel
import org.alsi.android.local.model.tv.TvChannelEntity

class TvChannelEntityMapper: EntityMapper<TvChannelEntity, TvChannel> {

    override fun mapFromEntity(entity: TvChannelEntity): TvChannel {
        return with(entity) {
            TvChannel(id, category.target.id, logoUri, number, title)
        }
    }

    override fun mapToEntity(domain: TvChannel): TvChannelEntity {
        return with(domain) {
            val entity = TvChannelEntity(id, logoUri, number, title)
            entity.category.targetId = categoryId
            entity
        }
    }
}