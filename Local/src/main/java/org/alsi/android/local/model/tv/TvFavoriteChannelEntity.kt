package org.alsi.android.local.model.tv

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class TvFavoriteChannelEntity (

        @Id var tvChannelId: Long,

        var userAccountId: Long
)