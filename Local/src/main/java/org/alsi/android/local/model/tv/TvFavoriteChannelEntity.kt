package org.alsi.android.local.model.tv

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class TvFavoriteChannelEntity (

        @Id var id: Long,

        var channelId: Long,

        var userLoginName: String
)