package org.alsi.android.local.framework.objectbox

import java.net.URI
import io.objectbox.converter.PropertyConverter

class UriConverter: PropertyConverter<URI, String> {

    override fun convertToDatabaseValue(entityProperty: URI?): String? {
        return entityProperty?.toString()
    }

    override fun convertToEntityProperty(databaseValue: String?): URI? {
        return if (databaseValue != null) URI.create(databaseValue) else null
    }
}