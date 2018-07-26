package org.alsi.android.domain.context.model

import org.alsi.android.domain.user.model.UserAccount
import org.alsi.android.domain.user.repository.AccountService
import javax.inject.Inject

/** Presentation context defined by the data on or from a streaming service and a subscriber (user)
 * registered and logged in to the service and subscribed to particular features, having service
 * related settings, remote control settings, etc.
 *
 * Presentation context created from user credentials and service configuration.
 *
 */
class PresentationContext(
        val serviceId: String,              // scalar parameter
        val type: ServicePresentationType,  // constant
        val account: UserAccount,           // returned upon login
        val repository: ServiceRepository,  // singleton of dagger2
        val session: ServiceSession,        // restored from the local store for given user ID
        val settings: ServiceSettings,      // created upon login (i.e. from login response)
        val device: DeviceSettings          // restored upon session start
)

interface ServiceRepository {
    fun withSession(session: ServiceSession): ServiceRepository
}

/** Subset of the session data repository for given user and a service. Rather implemented on top of local store.
 */
class ServiceSession

/** Subset of the streaming services data on particular service for given user ...
 */
class ServiceSettings

/** Subset of the streaming services data on particular service for given user ...
 */
class DeviceSettings

class StreamingSettingsRepository
{
    /** It's supposed that the settings repository will get cached login response data
     * in case the service settings are there or will execute extra configuration
     * request to get them
     */
    fun getPersonalSettings(account: UserAccount): ServiceSettings {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class DeviceDataRepository {
    fun getPersonalSettings(account: UserAccount): DeviceSettings {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class SessionRepository {
    fun getSession(account: UserAccount, serviceId: String): ServiceSession {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * This is input data to create Presentation Context.
 */
class StreamingServiceConfiguration (
        val serviceId: String,                      // scalar parameter
        val type: ServicePresentationType,          // constant
        val account: AccountService,                // singleton of dagger2, identified by service type
        val repository: ServiceRepository,          // singleton of dagger2, identified by service type
        val settings: StreamingSettingsRepository,  // singleton of dagger2, identified by service ID
        val device: DeviceDataRepository,           // singleton of dagger2, identified by service and user IDs
        val session: SessionRepository              // --//--
)

/** Presentation manager maintains list of available presentation contexts and provides use cases
 * to view models, which are bound to current context.
 *
 * It supplies use cases with bound repositories with injection modification ...
 */
class PresentationManager @Inject constructor(services: List<StreamingServiceConfiguration>)
{
    private var configurationById: Map<String, StreamingServiceConfiguration> = services.associateBy({it.serviceId}, {it})

    private var contextByServiceId: MutableMap<String, PresentationContext> = mutableMapOf()

    private var currentPresentationMap: MutableMap<ServicePresentationType, String> = mutableMapOf()

    init {
        TODO("Set default selection of context - 1st withing type or coming from restored configuration")
    }

    /** Get configuration of service required to create Service Presentation.
     *
     * Its solely for Use Cases responsible for Presentation Contexts creation.
     *
     * Use Case creates Presentation Context as a side (but necessary) effect of a user/service/ui
     * session opening/initialization/continuation
     */
    fun getServiceConfiguration(serviceId: String): StreamingServiceConfiguration? = configurationById[serviceId]

    /** Use Case creating context will use this method to set/update context
     */
    fun setPresentationContext(serviceId: String, context: PresentationContext) {
        contextByServiceId[serviceId] = context
    }

    /** Each Use Case should access their repository through the current presentation context
     * set for given type
     */
    fun provideContext(type: ServicePresentationType): PresentationContext? {
        return contextByServiceId[ currentPresentationMap[type] ]
    }

    /** There are presentation layer objects that will switch, e.g. from TV to VOD,
     * or another VOD presentation and they will use this.
     */
    fun switchContext(servicePresentationType: ServicePresentationType, serviceId: String) {
        currentPresentationMap[servicePresentationType] = serviceId
    }
}
