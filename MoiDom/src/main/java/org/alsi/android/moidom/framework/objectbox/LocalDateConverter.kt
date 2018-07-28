package org.alsi.android.moidom.framework.objectbox

import io.objectbox.converter.PropertyConverter
import org.joda.time.LocalDate

/**
 * Created on 7/27/18.
 */
class LocalDateConverter: PropertyConverter<LocalDate, Long> {

    override fun convertToDatabaseValue(entityProperty: LocalDate?): Long {
        return entityProperty?.toDateTimeAtStartOfDay()?.millis?: 0L
    }

    override fun convertToEntityProperty(databaseValue: Long?): LocalDate {
        return LocalDate(databaseValue?:0L)
    }
}