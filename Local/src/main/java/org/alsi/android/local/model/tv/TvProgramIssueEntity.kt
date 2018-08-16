package org.alsi.android.local.model.tv

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique
import org.alsi.android.local.framework.objectbox.UriConverter
import java.net.URI

@Entity
data class TvProgramIssueEntity (
        @Id var id: Long,

        @Unique var channelId: Long,
        var programId: Long? = null,

        var startMillis: Long? = null,
        var endMillis: Long? = null,

        var title: String? = null,
        var isTitleAvailable: Boolean = true,
        var description: String? = null,

        @Convert(converter = UriConverter::class, dbType = String::class)
        var videoStreamUri: URI? = null)