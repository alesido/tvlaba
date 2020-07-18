package org.alsi.android.local.model.tv

import io.objectbox.annotation.*
import io.objectbox.relation.ToOne

@Entity
data class TvChannelFeaturesEntity (

        @Id var id: Long = 0L,

        var hasArchive: Boolean = true,
        var hasSchedule: Boolean = true,
        var isPasswordProtected: Boolean = false,
        var hasMultipleLanguageAudioTracks: Boolean = false
){
        @Backlink
        lateinit var channel: ToOne<TvChannelEntity>
}