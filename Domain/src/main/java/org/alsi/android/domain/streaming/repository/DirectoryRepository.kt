package org.alsi.android.domain.streaming.repository

/** Each directory repository is attached to a certain streaming service.
 *
 * Knowing the service ID was primarily acquired to identify separate local
 * storage for service directory.
 */
open class DirectoryRepository(val streamingServiceId: Long)