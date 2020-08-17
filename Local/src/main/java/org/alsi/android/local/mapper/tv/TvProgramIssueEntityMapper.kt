package org.alsi.android.local.mapper.tv

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.tv.model.guide.TvProgramIssue
import org.alsi.android.domain.tv.model.guide.TvProgramTimeInterval
import org.alsi.android.local.model.tv.TvProgramIssueEntity
import org.alsi.android.local.model.tv.TvProgramLiveEntity

class TvProgramIssueEntityMapper: EntityMapper<TvProgramIssueEntity, TvProgramIssue> {

    override fun mapFromEntity(entity: TvProgramIssueEntity): TvProgramIssue {
        with(entity) {
            val s = startMillis
            val e = endMillis
            // TODO Add mapping to the rest of program properties
            return TvProgramIssue(channelId,
                    time = if (s != null && e != null) TvProgramTimeInterval(s, e) else null,
                    title = entity.title, description = entity.description)
        }
    }

    override fun mapToEntity(domain: TvProgramIssue): TvProgramIssueEntity {
        return with (domain) {
            // TODO Add mapping to the rest of program properties
            TvProgramIssueEntity(id = 0L, channelId = channelId, programId = programId,
                    startMillis = time?.startUnixTimeMillis, endMillis = time?.endUnixTimeMillis,
                    title = title, description = description)
        }
    }
}