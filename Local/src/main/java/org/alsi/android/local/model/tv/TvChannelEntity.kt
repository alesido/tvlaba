package org.alsi.android.local.model.tv

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import org.alsi.android.local.framework.objectbox.UriConverter
import java.net.URI

@Entity
data class TvChannelEntity (

        @Id(assignable = true) var id: Long,

        @Convert(converter = UriConverter::class, dbType = String::class)
        var logoUri: URI,

        var number: Int,

        var title: String

) {
    lateinit var category: ToOne<TvChannelCategoryEntity>

    lateinit var live: ToOne<TvProgramIssueEntity>
}