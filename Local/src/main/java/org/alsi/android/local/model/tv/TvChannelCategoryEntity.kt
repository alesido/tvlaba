package org.alsi.android.local.model.tv

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
data class TvChannelCategoryEntity(

        @Id var id: Long,
        var title: String,
        var logoReference: String) {

    @Backlink
    lateinit var channels: ToMany<TvChannelEntity>
}