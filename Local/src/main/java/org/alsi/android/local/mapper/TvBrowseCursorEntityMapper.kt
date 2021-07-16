package org.alsi.android.local.mapper

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.tv.model.session.TvBrowseCursor
import org.alsi.android.domain.tv.model.session.TvBrowseCursorReference
import org.alsi.android.domain.tv.model.session.TvBrowsePage
import org.alsi.android.local.model.tv.TvBrowseCursorEntity
import org.alsi.android.local.model.tv.TvBrowsePageProperty
import org.joda.time.LocalDate

class TvBrowseCursorEntityMapper: EntityMapper<TvBrowseCursorEntity, TvBrowseCursor> {

    override fun mapFromEntity(entity: TvBrowseCursorEntity): TvBrowseCursor {
        throw NotImplementedError("There is no trivial mapper from TvBrowseCursorEntity to TvBrowseCursor. See correspondent UC")
    }

    fun mapFromEntityToReference(entity: TvBrowseCursorEntity): TvBrowseCursorReference {
        return with (entity) {
            TvBrowseCursorReference(
                categoryId,
                channelId,
                scheduleDate,
                programId,
                page?.value ?: TvBrowsePage.UNKNOWN,
                timeStamp
            )
        }
    }

    override fun mapToEntity(domain: TvBrowseCursor): TvBrowseCursorEntity {
        return with (domain) {
            TvBrowseCursorEntity(id = 0L,
                categoryId = category?.id ?: 0L,
                channelId = channel?.id ?: 0L,
                scheduleDate = schedule?.date ?: LocalDate(),
                programId = program?.programId ?: 0L,
                page = TvBrowsePageProperty.valueByType[page],
                timeStamp = timeStamp)
        }
    }
}