package org.alsi.android.local.model.vod

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
data class VodDirectoryEntity(
    @Id(assignable = true) var id: Long = 0L,
    var timeStamp: Long? = null
) {
    @Backlink
    lateinit var sections: ToMany<VodSectionEntity>

    companion object {
        const val SINGLE_RECORD_DIRECTORY_ID = 1L
    }
}

@Entity
data class VodSectionEntity(
    @Id(assignable = true) var id: Long = 0L,
    var title: String? = null,
    var ordinal: Int? = null,
    var isSectionSubstitute: Boolean? = null
) {
    lateinit var directory: ToOne<VodDirectoryEntity>

    @Backlink
    lateinit var units: ToMany<VodUnitEntity>
}

@Entity
data class VodUnitEntity(
    @Id(assignable = true) var id: Long = 0L,
    var title: String? = null,
    var total: Int? = null,
    var ordinal: Int? = null
) {
    lateinit var section: ToOne<VodSectionEntity>
}
