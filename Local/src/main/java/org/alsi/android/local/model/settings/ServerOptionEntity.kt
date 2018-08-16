package org.alsi.android.local.model.settings

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ServerOptionEntity(
        @Id var id: Long,
        var reference: String, // URL or IP, or a conventional name
        var title: String,
        var description: String)
