package org.alsi.android.local.mapper.tv

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.tv.model.guide.TvProgramLive
import org.alsi.android.domain.tv.model.guide.TvProgramTimeInterval
import org.alsi.android.local.model.tv.TvProgramLiveEntity

class TvProgramLiveEntityMapper: EntityMapper<TvProgramLiveEntity, TvProgramLive> {

    override fun mapFromEntity(entity: TvProgramLiveEntity): TvProgramLive {
            val start = entity.startMillis
            val end = entity.endMillis
            return TvProgramLive(
                    if (start != null && end != null) TvProgramTimeInterval(start, end) else null,
                    entity.title,
                    entity.description)
    }

    override fun mapToEntity(domain: TvProgramLive): TvProgramLiveEntity {
        return with (domain) {
            TvProgramLiveEntity(0L, time?.startUnixTimeMillis, time?.endUnixTimeMillis, title, description)
        }
    }

}