package org.alsi.android.local.model.user

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import org.alsi.android.domain.user.model.SubscriptionPackage

@Entity
data class SubscriptionPackageEntity (

    @Id(assignable = true) var id: Long,

    var title: String? = "Default",

    var termMonths: Int? = 1,

    @Convert(converter = StringListConverter::class, dbType = String::class)
    var packets: List<String>? = listOf()
) {
    fun updateWith(source: SubscriptionPackage) {
        title = source.title
        termMonths = source.termMonths
        packets = source.packets
    }
}

class StringListConverter: PropertyConverter<List<String>, String> {
    override fun convertToEntityProperty(databaseValue: String?): List<String>
    = databaseValue?.split(",")?: listOf()

    override fun convertToDatabaseValue(entityProperty: List<String>?): String
    = entityProperty?.joinToString(",")?: ""
}