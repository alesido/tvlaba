package org.alsi.android.local.model.tv

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.relation.ToOne
import org.alsi.android.local.framework.objectbox.UriConverter
import java.net.URI

@Entity
data class TvChannelIndexEntity (

        @Id(assignable = true) var id: Long = 0L,

        @Index
        var categoryId: Long = 0L,

        var channelId: Long = 0L
)