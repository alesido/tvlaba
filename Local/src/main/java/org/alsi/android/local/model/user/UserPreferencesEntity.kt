package org.alsi.android.local.model.user

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import org.alsi.android.domain.user.model.FontSizeOption

@Entity
data class UserPreferencesEntity (

    @Id var id: Long,

    var loginRememberMe: Boolean? = false,

    @Convert(converter = FontSizeOptionConverter::class, dbType = Int::class)
    var fontSize: FontSizeOption? = FontSizeOption.MEDIUM
)

class FontSizeOptionConverter: PropertyConverter<FontSizeOption, Int> {

    override fun convertToEntityProperty(databaseValue: Int): FontSizeOption =
        if (databaseValue < FontSizeOption.values().size)
            FontSizeOption.values()[databaseValue] else FontSizeOption.MEDIUM

    override fun convertToDatabaseValue(entityProperty: FontSizeOption): Int = entityProperty.ordinal
}