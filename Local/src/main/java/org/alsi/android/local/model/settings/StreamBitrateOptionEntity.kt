package org.alsi.android.local.model.settings

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity data class StreamBitrateOptionEntity (
    @Id var id: Long,
    var value: Int,
    var title: String? = null
)