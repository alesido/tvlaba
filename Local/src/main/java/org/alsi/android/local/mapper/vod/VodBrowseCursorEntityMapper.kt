package org.alsi.android.local.mapper.vod

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvBrowseCursorReference
import org.alsi.android.domain.tv.model.session.TvBrowsePage
import org.alsi.android.domain.vod.model.session.VodBrowseCursor
import org.alsi.android.domain.vod.model.session.VodBrowseCursorReference
import org.alsi.android.domain.vod.model.session.VodBrowsePage
import org.alsi.android.local.model.tv.TvBrowseCursorEntity
import org.alsi.android.local.model.tv.TvBrowsePageProperty
import org.alsi.android.local.model.vod.VodBrowseCursorEntity
import org.alsi.android.local.model.vod.VodBrowsePageProperty
import org.joda.time.LocalDate

class VodBrowseCursorEntityMapper: EntityMapper<VodBrowseCursorEntity, VodBrowseCursor> {

    override fun mapFromEntity(entity: VodBrowseCursorEntity): VodBrowseCursor {
        throw NotImplementedError("There is no trivial mapper from VodBrowseCursorEntity to VodBrowseCursor. See correspondent UC")
    }

    fun mapFromEntityToReference(entity: VodBrowseCursorEntity): VodBrowseCursorReference {
        return with (entity) {
            VodBrowseCursorReference(
                sectionId,
                unitId,
                itemId,
                itemPosition,
                page?.value ?: VodBrowsePage.UNKNOWN,
                timeStamp
            )
        }
    }

    override fun mapToEntity(domain: VodBrowseCursor): VodBrowseCursorEntity {
        return with (domain) {
            VodBrowseCursorEntity(id = 0L,
                sectionId = section?.id ?: 0L,
                unitId = unit?.id ?: 0L,
                itemId = item?.id ?: 0L,
                itemPosition = itemPosition?: 0,
                page = VodBrowsePageProperty.valueByType[page],
                timeStamp = timeStamp)
        }
    }
}