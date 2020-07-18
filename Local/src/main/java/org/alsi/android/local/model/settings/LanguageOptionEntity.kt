package org.alsi.android.local.model.settings

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class LanguageOptionEntity (
        @Id var id: Long,
        @Unique var code: String,
        @Unique var name: String)