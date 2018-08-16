package org.alsi.android.domain.context.model

import org.alsi.android.domain.streaming.model.service.StreamingService
import org.alsi.android.domain.streaming.model.service.StreamingServiceKind
import org.alsi.android.domain.streaming.model.service.StreamingServiceRegistry
import javax.inject.Inject

/** Presentation manager maintains list of available presentation contexts and provides view models
 *  with use cases, ready bound to the current service context.
 *
 *  Its possible there are multiple services of the same kind, e.g. project uses external VOD service
 *  like we have in case of Moi Dom and MEGOGO. That is main reason to have this contexts
 *  manager-switcher.
 *
 *  Currently it is supposed that the service presentation context, which
 *  is Streaming Directory + Cursor, is fully defined by the service
 *  definition itself.
 *
 *  Cursor, in turn, is selected (fully identified in the database) by current streaming
 *  directory and user account selection. The both are, in their turn, fully identified by the
 *  service configuration, i.e. user account linked into a service (and shared between)
 *  and the directory is the intrinsic (identifying) part of.
 *
 *  Service configuration including linked account, core and additional repositories are defined
 *  in dependencies (dagger) module of an app (e.g. TV or mobile app)
 *
 *  Session repository doesn't belong to any particular service, it's shared between services
 *  (to be implemented with local database in shared Data module). It stores the directory
 *  cursor and session related settings.
 *
 */
class PresentationManager @Inject constructor(val registry: StreamingServiceRegistry)
{
    /** List of contexts available to switch to.
     */
    private var contexts: MutableMap<StreamingServiceKind, StreamingService> = mutableMapOf()

    private val presentationForServiceKind = hashMapOf(
                    StreamingServiceKind.TV to ServicePresentationType.TV_GUIDE,
                    StreamingServiceKind.VOD to ServicePresentationType.VOD_GUIDE
            )

    private val serviceKindForPresentation =
            presentationForServiceKind.entries.associateBy({ it.value }, {it.key})

    init {
        registry.servicesByKind.forEach {
            contexts[it.key] = it.value[0]
        }
    }

    /** Use Case creating context will use this method to set/update context
     */
    fun selectContext(serviceId: Long) {
        registry.serviceById[serviceId]?.let {
            contexts[it.kind] = it
        }
    }

    /** There are presentation layer objects that will switch, e.g. from TV to VOD,
     * or another VOD presentation and they will use this.
     */
    fun switchToContext(serviceId: Long) {
        selectContext(serviceId)
    }

    /** Each Use Case should access their repository through the current presentation context
     * set for given type
     */
    fun provideContext(presentationType: ServicePresentationType): StreamingService? {
        return contexts[ serviceKindForPresentation[presentationType]]
    }
}
