package org.alsi.android.local.model.tv

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import org.alsi.android.domain.implementation.model.IconType

@Entity
data class TvChannelCategoryEntity(

        @Id(assignable = true) var id: Long = 0L,
        var title: String? = null,

        @Convert(converter = IconTypeConverter::class, dbType = Long::class)
        var logoIconType: IconTypeProperty? = null,

        var logoReference: String? = null
)

enum class IconTypeProperty(val id: Long, val value: IconType) {
    UNKNOWN(0L, IconType.UNKNOWN),
    LOCAL_VECTOR(1L, IconType.LOCAL_VECTOR),
    LOCAL_RASTER(2L, IconType.LOCAL_RASTER),
    REMOTE_RASTER(3L, IconType.REMOTE_RASTER);
    companion object {
        val valueById = IconTypeProperty.values().map { it.id to it }.toMap()
        val valueByType = IconTypeProperty.values().map { it.value to it }.toMap()
    }
}

class IconTypeConverter: PropertyConverter<IconTypeProperty, Long> {
    override fun convertToDatabaseValue(entityProperty: IconTypeProperty?): Long = entityProperty?.id?: IconTypeProperty.UNKNOWN.id
    override fun convertToEntityProperty(databaseValue: Long?): IconTypeProperty = IconTypeProperty.valueById[databaseValue]?: IconTypeProperty.UNKNOWN
}