package org.alsi.android.local.model.vod

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.relation.ToOne

@Entity
data class VodPlayCursorEntity(

        @Id var id: Long = 0L,

        /**
         *  Multiuser. It is to store playback cursors individually, separate for each user.
         */
        var userLoginName: String = "guest",

        @Index
        var timeStamp: Long = 0L,

        var seekTime: Long = 0L
) {
        lateinit var playback: ToOne<VodPlaybackEntity>
}