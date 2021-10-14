package org.alsi.android.local.model.settings

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ApiServerOptionEntity(
    @Id var id: Long,
    var title: String,
    var baseUrl: String
)
