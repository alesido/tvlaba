package org.alsi.android.local.model.tv

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.relation.ToOne

@Entity
data class TvPlayCursorEntity(

        @Id var id: Long = 0L,

        var userLoginName: String = "guest",

        /** A TV channel may belong to several categories. It may be sensible to
         * know from which category we navigated to an item we start a playback
         * for.
         */
        @Index
        var categoryId: Long = 0L,

        @Index
        var timeStamp: Long = 0L,

        var seekTime: Long = 0L
) {
        lateinit var playback: ToOne<TvPlaybackEntity>
}