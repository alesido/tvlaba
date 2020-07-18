package org.alsi.android.local.mapper.tv

import org.alsi.android.data.framework.mapper.EntityMapper
import org.alsi.android.domain.tv.model.guide.TvChannelFeatures
import org.alsi.android.local.model.tv.TvChannelFeaturesEntity

class TvChannelFeaturesEntityMapper: EntityMapper<TvChannelFeaturesEntity, TvChannelFeatures> {

    override fun mapFromEntity(entity: TvChannelFeaturesEntity): TvChannelFeatures {
        return with(entity) {
            TvChannelFeatures(hasArchive, hasSchedule, isPasswordProtected, hasMultipleLanguageAudioTracks)
        }
    }

    override fun mapToEntity(domain: TvChannelFeatures): TvChannelFeaturesEntity {
        return with(domain) {
            TvChannelFeaturesEntity(0L, hasArchive, hasSchedule, isPasswordProtected, hasMultipleLanguageAudioTracks)
        }
    }
}