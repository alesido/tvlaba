package org.alsi.android.domain.streaming.model

import org.alsi.android.domain.user.repository.AccountDataService

open class StreamingService (
        val id: Long,
        val providerId: Long,
        val kind: StreamingServiceKind,
        val tag: String,
        val account: AccountDataService,
        val directory: DirectoryRepository,
        val settings: SettingsRepository,  // singleton of dagger2, identified by service ID
        val device: DeviceDataRepository,  // singleton of dagger2, identified by service and user IDs
        val session: SessionRepository) {

    companion object {
        const val TV = "tv"
        const val VOD = "vod"
    }
}

class StreamingServiceRegistry : ArrayList<StreamingService>() {

    val serviceById: Map<Long, StreamingService> = associateBy({it.id},{it})
    val serviceByTag: Map<String, StreamingService> = associateBy({it.tag},{it})

    /** TODO Make immutable service-by-kind index. Check "https://stackoverflow.com/questions/37931676/how-to-turn-a-mutable-collection-into-an-immutable-one"
     */
    val servicesByKind: MutableMap<StreamingServiceKind, MutableList<StreamingService>> = let { _ ->
        val result: HashMap<StreamingServiceKind, MutableList<StreamingService>> = HashMap()
        forEach {
            if (null == result[it.kind]) {
                result[it.kind] = mutableListOf()
            }
            result[it.kind]?.add(it)
        }
        result
    }
}

/** Each directory repository is attached to a certain streaming service.
 *
 * Knowing the service ID was primarily acquired to identify separate local
 * storage for service directory.
 */
open class DirectoryRepository(val streamingServiceId: Long)

open class SettingsRepository
{
//    /** It's supposed that the settings repository will get cached login response data
//     * in case the service settings are there or will execute extra configuration
//     * request to get them
//     */
//    fun getPersonalSettings(account: UserAccount): ServiceSettings {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}

open class DeviceDataRepository {
//    fun getPersonalSettings(account: UserAccount): DeviceSettings {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}

open class SessionRepository {
//    fun getSession(account: UserAccount, serviceId: String): ServiceSession {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
}


