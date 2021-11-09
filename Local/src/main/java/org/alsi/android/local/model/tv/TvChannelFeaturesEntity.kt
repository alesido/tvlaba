package org.alsi.android.local.model.tv

import io.objectbox.annotation.*
import io.objectbox.relation.ToOne
import org.alsi.android.domain.tv.model.guide.TvChannelFeatures

@Entity
data class TvChannelFeaturesEntity (

        @Id var id: Long = 0L,

        var hasArchive: Boolean = true,
        var hasSchedule: Boolean = true,
        var isPasswordProtected: Boolean = false,
        var hasMultipleLanguageAudioTracks: Boolean = false
) {
        fun updateWith(source: TvChannelFeatures) {
                hasArchive = source.hasArchive
                hasSchedule = source.hasSchedule
                isPasswordProtected = source.isPasswordProtected
                hasMultipleLanguageAudioTracks = source.hasMultipleLanguageAudioTracks
        }
}
