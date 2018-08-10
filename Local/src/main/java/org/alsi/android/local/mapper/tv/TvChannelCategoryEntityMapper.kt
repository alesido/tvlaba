package org.alsi.android.local.mapper.tv

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.implementation.model.TypedIconReference
import org.alsi.android.domain.tv.model.guide.TvChannelCategory
import org.alsi.android.local.model.tv.IconTypeProperty
import org.alsi.android.local.model.tv.TvChannelCategoryEntity

class TvChannelCategoryEntityMapper: EntityMapper<TvChannelCategoryEntity, TvChannelCategory> {

    override fun mapFromEntity(entity: TvChannelCategoryEntity): TvChannelCategory {
        return with(entity) {
            TvChannelCategory(id, title, logo = TypedIconReference(logoIconType.value, logoReference))
        }
    }

    override fun mapToEntity(domain: TvChannelCategory): TvChannelCategoryEntity {
        return with(domain) {
            TvChannelCategoryEntity(
                id, title,
                IconTypeProperty.valueByType[logo.kind]?: IconTypeProperty.UNKNOWN,
                logo.reference)
        }
    }
}