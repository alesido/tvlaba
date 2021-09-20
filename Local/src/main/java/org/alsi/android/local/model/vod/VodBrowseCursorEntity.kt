package org.alsi.android.local.model.vod

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import org.alsi.android.domain.vod.model.session.VodBrowsePage

@Entity
data class VodBrowseCursorEntity(

        @Id var id: Long = 0L,

        var userLoginName: String = "guest",

        var sectionId: Long = 0L,
        var unitId: Long = 0L,

        var itemId: Long = 0L,
        var itemPosition: Int = 0,

        @Convert(converter = VodBrowsePageConverter::class, dbType = Long::class)
        var page: VodBrowsePageProperty? = null,

        var timeStamp: Long = 0L,
)

enum class VodBrowsePageProperty(val id: Long, val value: VodBrowsePage) {
        UNKNOWN(0L, VodBrowsePage.UNKNOWN),
        SECTIONS(1L, VodBrowsePage.SECTIONS),
        UNITS(2L, VodBrowsePage.UNITS),
        ITEM(3L, VodBrowsePage.ITEM),
        PLAYBACK (4L, VodBrowsePage.PLAYBACK);
        companion object {
                val valueById = values().map { it.id to it }.toMap()
                val valueByType = values().map { it.value to it }.toMap()
        }
}

class VodBrowsePageConverter: PropertyConverter<VodBrowsePageProperty, Long> {
        override fun convertToDatabaseValue(entityProperty: VodBrowsePageProperty?): Long = entityProperty?.id?: VodBrowsePageProperty.UNKNOWN.id
        override fun convertToEntityProperty(databaseValue: Long?): VodBrowsePageProperty = VodBrowsePageProperty.valueById[databaseValue]?: VodBrowsePageProperty.UNKNOWN
}
