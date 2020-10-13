package org.alsi.android.local.model.settings

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

/** Service settings of a user of a particular provider or service.
 *
 *  NOTE Not sure if scope of the settings may include particular service only. Now it's
 *  the same for all services of a provider. Just taking this possibility into account here
 *  by adding scope type and ID properties.
 *
 */
@Entity
data class ServiceSettingsEntity(

        @Id(assignable = true) var id: Long,

        var scopeTypeOrdinal: Int, // see constants below
        var scopeId: Long, // provider or service ID depending on the scope type

        var accountId: Long) {

    lateinit var server: ToOne<ServerOptionEntity>
    lateinit var language: ToOne<LanguageOptionEntity>
    lateinit var device: ToOne<DeviceModelOptionEntity>

    constructor() : this(0L, 1, 0L, 0L)

    companion object {
        const val SCOPE_PROVIDER = 1
        const val SCOPE_SERVICE = 2
    }
}
