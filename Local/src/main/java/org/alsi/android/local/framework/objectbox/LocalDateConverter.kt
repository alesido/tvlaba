package org.alsi.android.local.framework.objectbox

import io.objectbox.converter.PropertyConverter
import org.joda.time.LocalDate

class LocalDateConverter: PropertyConverter<LocalDate, Long> {

    override fun convertToDatabaseValue(entityProperty: LocalDate?): Long {
        return entityProperty?.toDateTimeAtStartOfDay()?.millis?: 0L
    }

    override fun convertToEntityProperty(databaseValue: Long?): LocalDate {
        return LocalDate(databaseValue?:0L)
    }
}