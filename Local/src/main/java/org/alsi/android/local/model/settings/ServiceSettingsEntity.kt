package org.alsi.android.local.model.settings

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import io.objectbox.relation.ToOne
import org.alsi.android.domain.streaming.model.service.StreamingServiceFeature
import java.util.*

/** Service settings of a user of a particular provider or service.
 *
 *  NOTE Not sure if scope of the settings may include particular service only. Now it's
 *  the same for all services of a provider. Just taking this possibility into account here
 *  by adding scope type and ID properties.
 *
 */
@Entity
data class ServiceSettingsEntity(

        @Id(assignable = true) var id: Long? = 0L,

        var scopeTypeOrdinal: Int? = 1, // see constants below
        var scopeId: Long? = 0L, // provider or service ID depending on the scope type

        var accountId: Long? = 0L,

        @Convert(converter = FeaturesPropertyConverter::class, dbType = String::class)
        var features: EnumSet<StreamingServiceFeature>? = null,

        var bitrate: Int? = null,
        var cacheSize: Long? = null,
) {

    lateinit var server: ToOne<ServerOptionEntity>
    lateinit var api: ToOne<ApiServerOptionEntity>
    lateinit var language: ToOne<LanguageOptionEntity>
    lateinit var device: ToOne<DeviceModelOptionEntity>

    companion object {
        const val SCOPE_PROVIDER = 1
        const val SCOPE_SERVICE = 2
    }
}

class FeaturesPropertyConverter: PropertyConverter<EnumSet<StreamingServiceFeature>, String> {
    override fun convertToEntityProperty(databaseValue: String?): EnumSet<StreamingServiceFeature> {
        val result = EnumSet.noneOf(StreamingServiceFeature::class.java)
        databaseValue?.split(",")?.forEach {
            try { result.add(StreamingServiceFeature.valueOf(it)) }
            catch (ignore: IllegalArgumentException) {}
        }
        return result
    }
    override fun convertToDatabaseValue(entityProperty: EnumSet<StreamingServiceFeature>?): String
        = entityProperty?.toArray()?.joinToString { it.toString() } ?: ""
}
